package io.systom.coin.service;

import com.google.gson.Gson;
import io.systom.coin.exception.AuthenticationException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.exception.ParameterException;
import io.systom.coin.exception.RequestException;
import io.systom.coin.model.*;
import io.systom.coin.utils.DockerUtils;
import io.systom.coin.utils.TaskFuture;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import static io.systom.coin.service.GoodsService.BOT_USER_ID;

@Service
public class TaskService {

    private static Logger logger = LoggerFactory.getLogger(TaskService.class);
    private static Logger backTestLogger = LoggerFactory.getLogger("backTestLogger");

    @Value("${backtest.apiServerUrl}")
    private String apiServerUrl;

    @Autowired
    private SqlSession sqlSession;
    @Autowired
    private DockerUtils dockerUtils;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private InvestGoodsService investGoodsService;

    private static Map<String, Task> waitTaskList = new ConcurrentHashMap<>();
    private static ConcurrentMap<String, TaskFuture> backTestResult = new ConcurrentHashMap<>();

    private final String RESULT_JSON = "resultJson";

    public Task isWaitTask(String taskId) {
        if (taskId != null && waitTaskList.get(taskId) != null) {
            return waitTaskList.get(taskId);
        } else {
            return null;
        }
    }

    public TaskResult syncBackTest(Task task) throws TimeoutException, ParameterException, OperationException {
        if (task.getUserId() == null || !identityService.isManager(task.getUserId())) {
            throw new AuthenticationException();
        }
        isNotEmpty(task.getStrategyId(), "strategyId");
        isNotEmpty(task.getExchange(), "exchange");
        isNotEmpty(task.getCoinUnit(), "coinUnit");
        isNotEmpty(task.getBaseUnit(), "baseUnit");
        isNotEmpty(task.getCashUnit(), "cashUnit");
        isNotEmpty(task.getStartDate(), "StartDate");
        isNotEmpty(task.getEndDate(), "endDate");

        logger.debug("[ BACK TEST ] RUN {}", task);

        task.setId(UUID.randomUUID().toString());
        waitTaskList.put(task.getId(), task);
        logger.info("Generator Task taskId: {}", task.getId());

        List<String> envList = mergeTaskEnvRequire(task.getSignalRunEnv());
        List<String> cmdList = mergeTaskCmdRequire(task.getSignalRunCmd());

        try {

            dockerUtils.syncRun(task.getId(), envList, cmdList);

//            RestTemplate restTemplate = new RestTemplate();
//            String taskResultJson = restTemplate.getForObject("http://localhost:8080/result.json", String.class);
//            TaskResult taskResult = new Gson().fromJson(taskResultJson, TaskResult.class);
//            registerBackTestResult(task.getId(), taskResult);

        } catch (Throwable t) {
            logger.error("", t);
            throw new OperationException("[FAIL] Running BackTest.");
        }
        TaskResult taskResult = null;
        try {
            TaskFuture<TaskResult> future = backTestResult.get(task.getId());
            taskResult = future.take();
            if (future != null) {
                backTestLogger.info("[{}] BackTest Task Result catch!", task.getId());
            }
            backTestLogger.info("[{}] BackTest Successful.", task.getId());
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] Not Catch Performance");
        } finally {
            waitTaskList.remove(task.getId());
        }
        return taskResult;
    }


    public String getTaskModel(String taskId) throws ParameterException, OperationException {
        Task task = waitTaskList.get(taskId);
        if (task == null) {
            throw new RequestException("invalid taskId");
        }
        StrategyDeployVersion deployVersion = new StrategyDeployVersion();
        deployVersion.setId(task.getStrategyId());
        deployVersion.setVersion(task.getVersion());
        try {
            backTestLogger.info("[{}] Download Modal.", taskId);
            return sqlSession.selectOne("strategyDeploy.getStrategyModel", deployVersion);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] sql execute");
        }
    }

    public TaskResult registerBackTestResult(String taskId, TaskResult taskResult) {
        if (isWaitTask(taskId) != null) {
            TaskFuture<TaskResult> taskFuture = new TaskFuture();
            taskFuture.offer(taskResult);
            backTestResult.put(taskId, taskFuture);
            backTestLogger.info("[{}] BackTest Result Saved. {}", taskId, taskResult);
            return taskResult;
        } else {
            return null;
        }
    }

    protected List<String> mergeTaskEnvRequire(List<String> env){
        List<String> tmpEnv = new ArrayList<>();
        tmpEnv.addAll(env);
        return tmpEnv;
    }

    protected List<String> mergeTaskCmdRequire(List<String> cmd){
        List<String> tmpCmd = new ArrayList<>();
        tmpCmd.addAll(cmd);
        tmpCmd.add("api_server_url=" + apiServerUrl);
        return tmpCmd;
    }

    public Goods createGoodsBackTest(Task task) throws TimeoutException, ParseException {
        if (!identityService.isManager(task.getUserId())) {
            throw new AuthenticationException();
        }

        TaskResult taskResult = syncBackTest(task);
        if (!"success".equalsIgnoreCase(taskResult.getStatus())) {
            throw new OperationException("[Fail] BackTest");
        }
        Map<String, Float> cumReturns = taskResult.getResult().getCumReturns();
        List<MonthlyReturn> MonthlyReturnList = monthlyLastDateReturnPct(cumReturns);
        float sumMrp = 0;
        float avgMRp = 0;
        int mRpSize = MonthlyReturnList.size();
        for (int i=0; i < mRpSize; i++){
            sumMrp += MonthlyReturnList.get(i).getReturnPct();
        }
        if (sumMrp > 0 && mRpSize > 0) {
            avgMRp = sumMrp / mRpSize;
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("goodsId", task.getGoodsId());
            params.put("testReturnPct", avgMRp);
            params.put("testMonthlyReturn", new Gson().toJson(MonthlyReturnList));
            int changeRow = sqlSession.update("goods.createGoodsBackTest", params);
            if (changeRow != 1) {
                logger.error("[FAIL] sql execute. changeRow: {}, params: {}", changeRow, params);
                throw new OperationException("[FAIL] sql execute. changeRow: {}" + changeRow);
            }

            InvestGoods botInvestGoods = investGoodsService.findInvestGoodsByUser(task.getGoodsId(), BOT_USER_ID);
            tradeService.insertTradeHistory(botInvestGoods.getId(), taskResult.getResult().getTradeHistory());

        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] sql execute");
        }

        return goodsService.getGoods(task.getGoodsId(), task.getUserId());
    }

    protected List<MonthlyReturn> monthlyLastDateReturnPct(Map<String, Float> cumReturnPct) throws ParseException {
        String tmpDate = null;

        Map<String, Float> lastRp = new LinkedHashMap<>();
        Iterator<Map.Entry<String, Float>> iterator = cumReturnPct.entrySet().iterator();

        while(iterator.hasNext()) {
            Map.Entry<String, Float> cumReturn = iterator.next();
            String yyyymm = cumReturn.getKey();
            if (!Pattern.matches("^[0-9]*$", yyyymm)) {
                continue;
            } else {
                yyyymm = yyyymm.substring(0, 6);
            }

            float rp = cumReturn.getValue().floatValue();
            lastRp.put(yyyymm, rp);
            if (tmpDate == null || !tmpDate.equals(yyyymm)) {
                tmpDate = yyyymm;
            }
        }
        float diff = 0f;
        List<MonthlyReturn> monthlyReturn = new ArrayList<>();
        iterator = lastRp.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, Float> lastCumReturn = iterator.next();
            float tmpRp = lastCumReturn.getValue();
            float rp = tmpRp - diff;
            diff = tmpRp;
            MonthlyReturn mr = new MonthlyReturn();
            mr.setDate(lastCumReturn.getKey());
            mr.setReturnPct(rp);
            monthlyReturn.add(mr);
        }
        return monthlyReturn;
    }













//    protected void recordBackTestPerformance(int investGoodsId, TaskResult.Result result) {
//        try {
//            result.setId(investGoodsId);
//            int changeRow = sqlSession.insert("backtest.recordPerformance", result);
//            logger.debug("recordPerformance row: {}", changeRow);
//        } catch (Exception e) {
//
//        }
//    }

//    protected void recordBackTestTradeHistory(int investGoodsId, List<TaskResult.Result.Trade> trades) {
//        trades.forEach(trade -> {
//            trade.setId(investGoodsId);
//        });
//        int changeRow = sqlSession.insert("backtest.recordTradeHistory", trades);
//        logger.debug("recordTradeHistory row: {}", changeRow);
//    }

//    protected void recordBackTestValueHistory(int investGoodsId, Map<Long, Float> equities, Map<Long, Float> cumReturns, Map<Long, Float> drawdowns){
//        List<TaskResult.Result.Value> values = new ArrayList<>();
//        Iterator<Map.Entry<Long, Float>> iterator = equities.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<Long, Float> entry = iterator.next();
//            Long ts = entry.getKey();
//            TaskResult.Result.Value v = new TaskResult.Result.Value();
//            v.setId(investGoodsId);
//            v.setTimestamp(ts);
//            v.setEquity(entry.getValue());
//            if (cumReturns.get(ts) != null) {
//                v.setCumReturn(cumReturns.get(ts));
//                cumReturns.remove(ts);
//            }
//            if (drawdowns.get(ts) != null) {
//                v.setDrawdown(drawdowns.get(ts));
//                drawdowns.remove(ts);
//            }
//            values.add(v);
//        }
//
//        iterator = cumReturns.entrySet().iterator();
//        while(iterator.hasNext()){
//            Map.Entry<Long, Float> entry = iterator.next();
//            Long ts = entry.getKey();
//            TaskResult.Result.Value v = new TaskResult.Result.Value();
//            v.setId(investGoodsId);
//            v.setTimestamp(ts);
//            v.setCumReturn(cumReturns.get(ts));
//            if (drawdowns.get(ts) != null) {
//                v.setDrawdown(drawdowns.get(ts));
//                drawdowns.remove(ts);
//            }
//            values.add(v);
//        }
//
//        iterator = drawdowns.entrySet().iterator();
//        while(iterator.hasNext()){
//            Map.Entry<Long, Float> entry = iterator.next();
//            Long ts = entry.getKey();
//            TaskResult.Result.Value v = new TaskResult.Result.Value();
//            v.setId(investGoodsId);
//            v.setTimestamp(ts);
//            v.setDrawdown(drawdowns.get(ts));
//            values.add(v);
//        }
//        int changeRow = sqlSession.insert("backtest.recordValueHistory", values);
//        logger.debug("recordValueHistory row: {}", changeRow);
//    }






//    public Task runAgentTask(String userId, String accessToken, Integer agentId, boolean isLiveMode) throws ParameterException, OperationException {
////        TODO 에이전트 cctrader 변경
//        throw new OperationException("개발예정");
////
////        Task task = getAgentTaskFromId(agentId);
////
////        ExchangeKey exchangeKey = exchangeService.selectExchangeKey(new ExchangeKey(task.getExchangeKeyId(), userId));
////        RunBackTestRequest.ExchangeAuth exchangeAuth = null;
////        String exchangeName = exchangeKey.getExchange();
////
////        List<KeyValuePair> environmentList = new ArrayList<>();
////        if(isLiveMode) {
////            environmentList.add(new KeyValuePair().withName(exchangeName + "_key").withValue(exchangeAuth.getKey()));
////            environmentList.add(new KeyValuePair().withName(exchangeName + "_secret").withValue(exchangeAuth.getSecret()));
////        }
////        environmentList.add(new KeyValuePair().withName("exchangeList").withValue(exchangeName));
////        environmentList.add(new KeyValuePair().withName("user_id").withValue(userId));
////        environmentList.add(new KeyValuePair().withName("access_token").withValue(accessToken));
////        environmentList.add(new KeyValuePair().withName("agent_id").withValue(String.valueOf(agentId)));
////
////        logger.debug("[ LIVE={} ] RUN {}", isLiveMode, task);
////        RunTaskResult result = null;
////        try {
////            result = ecsUtils.runTask(task, environmentList);
////        } catch (Exception e){
////            logger.error("", e);
////            throw new OperationException("[FAIL] ecs task syncRun error");
////        }
////
////        String ecsTaskId = parseTaskId(result);
////        task.setEcsTaskId(ecsTaskId);
////        logger.info("starting ecs: {}", ecsTaskId);
////        Agent agent = new Agent();
////        agent.setId(task.getId());
////        agent.setEcsTaskId(ecsTaskId);
////        agent.setState(Agent.STATE_RUN);
////        agentService.updateAgent(agent);
////        return task;
//    }
//
//
//    public Task stopAgentTask(String userId, Integer agentId) throws ParameterException, OperationException {
////        TODO 에이전트 정지
//        throw new OperationException("개발예정");
////        Agent agent = agentService.getAgent(agentId);
////        Task task = new Task();
////        task.setEcsTaskId(agent.getEcsTaskId());
////        StopTaskResult stopTaskResult = null;
////        try {
////            stopTaskResult = ecsUtils.stopTask(task.getEcsTaskId(), "User stop request : " + userId + ", " + agentId);
////        } catch (Exception e){
////            logger.error("", e);
////            throw new OperationException("[FAIL] ecs task syncRun error");
////        }
////        com.amazonaws.services.ecs.model.Task stopedTask = stopTaskResult.getTask();
////        logger.debug("Stopped task : {}", stopedTask);
////        agent.setState(Agent.STATE_STOP);
////        agentService.updateAgent(agent);
////        return task;
//
//    }
//
//
//    private String parseTaskId(RunTaskResult result) {
//        return result.getTasks().get(0).getTaskArn().split("/")[1];
//    }
//
//    private Task getAgentTaskFromId(Integer agentId) throws ParameterException, OperationException {
//
//        //agent 테이블을 읽어서 Task에 채워준다.
//        Agent agent = agentService.getAgent(agentId);
//        ExchangeKey exchangeKey = exchangeService.selectExchangeKey(new ExchangeKey(agent.getExchangeKeyId(), agent.getUserId()));
//        Task task = agent.cloneTask();
//        task.setExchange(exchangeKey.getExchange());
//        task.setExchangeKeyId(agent.getExchangeKeyId());
//        return task;
//    }
//
//
//    public List<Task> getBackTestHistory(String strategyId) throws OperationException {
//        try {
//            return sqlSession.selectList("backtest.selectHistory", strategyId);
//        } catch (Exception e){
//            logger.error("", e);
//            throw new OperationException("[FAIL] Select Test History");
//        }
//    }
//
//    public void deleteBackTestHistory(OldStrategy oldStrategy) throws OperationException {
//        try {
//            sqlSession.delete("backtest.deleteTestHistory", oldStrategy);
//        } catch (Exception e){
//            logger.error("", e);
//            throw new OperationException("[FAIL] Select Test History");
//        }
//    }
//


    private void isNotEmpty(String field, String label) throws ParameterException {
        if(field == null || "".equals(field)){
            throw new ParameterException(label);
        }
    }
    private void isNotEmpty(Integer field, String label) throws ParameterException {
        if(field == null){
            throw new ParameterException(label);
        }
    }
    private void isNotZero(float field, String label) throws ParameterException {
        if(field == 0.0f){
            throw new ParameterException(label);
        }
    }

    public void testTask(String task_id) {
        Task task = new Task();
        task.setId(task_id);
        task.setUserId("joonwoo");
        task.setStrategyId(18);
        waitTaskList.put(task_id, task);
    }
}