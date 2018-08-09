package io.systom.coin.service;

import com.amazonaws.services.ecs.model.Task;
import com.amazonaws.util.json.Jackson;
import io.systom.coin.exception.AuthenticationException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.exception.ParameterException;
import io.systom.coin.exception.RequestException;
import io.systom.coin.model.*;
import io.systom.coin.utils.DockerUtils;
import io.systom.coin.utils.EcsUtils;
import io.systom.coin.utils.TaskFuture;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;

@Service
public class TaskService {

    private static Logger logger = LoggerFactory.getLogger(TaskService.class);
    private static Logger backTestLogger = LoggerFactory.getLogger("backTestLogger");

    @Autowired
    private SqlSession sqlSession;
    @Autowired
    private DockerUtils dockerUtils;
    @Autowired
    private EcsUtils ecsUtils;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private InvestGoodsService investGoodsService;

    private static Map<String, TraderTask> waitTaskList;
    private static ConcurrentMap<String, TaskFuture> backTestResult = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        waitTaskList = new ConcurrentHashMap<>();
    }

    public TraderTask isWaitTask(String taskId) {
        if (taskId != null && waitTaskList.get(taskId) != null) {
            return waitTaskList.get(taskId);
        } else {
            return null;
        }
    }

    public TraderTaskResult syncBackTest(TraderTask traderTask) throws TimeoutException, ParameterException, OperationException {
        if (traderTask.getUserId() == null || !identityService.isManager(traderTask.getUserId())) {
            throw new AuthenticationException();
        }
        isNotEmpty(traderTask.getStrategyId(), "strategyId");
        isNotEmpty(traderTask.getExchange(), "exchange");
        isNotEmpty(traderTask.getCoinUnit(), "coinUnit");
        isNotEmpty(traderTask.getBaseUnit(), "baseUnit");
        isNotEmpty(traderTask.getCashUnit(), "cashUnit");
        isNotEmpty(traderTask.getStartDate(), "StartDate");
        isNotEmpty(traderTask.getEndDate(), "endDate");

        logger.debug("[ BACK TEST ] RUN {}", traderTask);

        traderTask.setId(UUID.randomUUID().toString());
        waitTaskList.put(traderTask.getId(), traderTask);
        logger.info("Generator TraderTask taskId: {}", traderTask.getId());

        try {
            dockerUtils.syncRun(traderTask);
            // 결과 저장에 약간의 시간딜레이를 준다.
            Thread.sleep(500);
        } catch (Throwable t) {
            logger.error("", t);
            throw new OperationException("[FAIL] Running BackTest.");
        }
        TraderTaskResult traderTaskResult = null;
        try {
            TaskFuture<TraderTaskResult> future = backTestResult.get(traderTask.getId());
            traderTaskResult = future.take();
            if (future != null) {
                backTestLogger.info("[{}] BackTest TraderTask Result catch!", traderTask.getId());
            }
            backTestLogger.info("[{}] BackTest Successful.", traderTask.getId());
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] Not Catch Performance");
        } finally {
            waitTaskList.remove(traderTask.getId());
        }
        return traderTaskResult;
    }


    public String getTaskModel(String taskId) throws ParameterException, OperationException {
        TraderTask traderTask = waitTaskList.get(taskId);
        if (traderTask == null) {
            throw new RequestException("invalid taskId");
        }
        StrategyDeployVersion deployVersion = new StrategyDeployVersion();
        deployVersion.setId(traderTask.getStrategyId());
        deployVersion.setVersion(traderTask.getVersion());
        try {
            backTestLogger.info("[{}] Download Modal.", taskId);
            String model = sqlSession.selectOne("strategyDeploy.getStrategyModel", deployVersion);
            return model;
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] sql execute");
        } finally {
            if("live".equals(traderTask.getSessionType())) {
                waitTaskList.remove(taskId);
            }
        }
    }

    public TraderTaskResult registerBackTestResult(String taskId, TraderTaskResult traderTaskResult) {
        if (isWaitTask(taskId) != null) {
            TaskFuture<TraderTaskResult> taskFuture = new TaskFuture();
            taskFuture.offer(traderTaskResult);
            backTestResult.put(taskId, taskFuture);
            backTestLogger.info("[{}] BackTest Result Saved. {}", taskId, traderTaskResult);
            return traderTaskResult;
        } else {
            return null;
        }
    }

    public Goods createGoodsBackTest(TraderTask traderTask) throws TimeoutException, ParseException {
        if (!identityService.isManager(traderTask.getUserId())) {
            throw new AuthenticationException();
        }

        TraderTaskResult traderTaskResult = syncBackTest(traderTask);
        if (!"success".equalsIgnoreCase(traderTaskResult.getStatus())) {
            throw new OperationException("[Fail] BackTest");
        }
        List<MonthlyReturn> MonthlyReturnList = new ArrayList<>();
        Map<String, Float> monthlyCumReturns = traderTaskResult.getResult().getMonthlyCumReturns();
        Iterator<Map.Entry<String, Float>> iterator = monthlyCumReturns.entrySet().iterator();
        float max = 0;
        float min = 0;
        while (iterator.hasNext()) {
            Map.Entry<String, Float> entry = iterator.next();
            MonthlyReturnList.add(new MonthlyReturn(entry.getKey(), entry.getValue()));
            if (entry.getValue().floatValue() > max) {
                max = entry.getValue();
            }
            if (entry.getValue().floatValue() < min) {
                min = entry.getValue();
            }
        }
        try {
            GoodsTestResult goodsTestResult = new GoodsTestResult();
            goodsTestResult.setId(traderTask.getGoodsId());
//            goodsTestResult.setTestMaxReturnsPct((int) traderTaskResult.getResult().getMaxReturnsPct());
//            goodsTestResult.setTestMaxDrawDownPct((int) traderTaskResult.getResult().getMaxDrawdownPct());
            goodsTestResult.setTestMaxMonthlyPct(Float.parseFloat(String.format("%.1f", max)));
            goodsTestResult.setTestMinMonthlyPct(Float.parseFloat(String.format("%.1f", min)));
            goodsTestResult.setTestMonthlyReturnList(MonthlyReturnList);
            goodsTestResult.setTradeHistory(traderTaskResult.getResult().getTradeHistory());
            Map<String, Object> params = new HashMap<>();
            params.put("goodsId", traderTask.getGoodsId());
            params.put("testResult", Jackson.toJsonPrettyString(goodsTestResult));
            int changeRow = sqlSession.update("goods.createGoodsBackTest", params);
            if (changeRow != 1) {
                logger.error("[FAIL] sql execute. changeRow: {}", changeRow);
                throw new OperationException("[FAIL] sql execute. changeRow: {}" + changeRow);
            }

        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] sql execute");
        }

        return goodsService.getGoods(traderTask.getGoodsId(), traderTask.getUserId());
    }


    public Task liveTaskRun(TraderTask traderTask) {
        if (!identityService.isManager(traderTask.getUserId())) {
            logger.info("접근권환 없는 사용자 요청. 사용자: {}", traderTask.getUserId());
            throw new AuthenticationException();
        }
        Goods registerGoods = goodsService.getGoods(traderTask.getGoodsId());
        if (traderTask.getGoodsId() == null || registerGoods == null) {
            throw new ParameterException("goodsId");
        }
        if (waitTaskList.get(registerGoods.getId().toString()) != null) {
            throw new OperationException("It is already in progress.");
        }
        if (registerGoods.getTaskEcsId() != null) {
            Task task = ecsUtils.getDescribeTasks(registerGoods.getTaskEcsId());
            if ("RUNNING".equalsIgnoreCase(task.getLastStatus())) {
                throw new OperationException("It is already in progress.");
            }
        }
        String taskId = registerGoods.getId().toString();
        traderTask.setId(taskId);
        traderTask.setStrategyId(registerGoods.getStrategyId());
        traderTask.setVersion(registerGoods.getVersion());
        traderTask.setStartDate(registerGoods.getInvestStart());
        traderTask.setExchange(registerGoods.getExchange());
        traderTask.setCoinUnit(registerGoods.getCoinUnit());
        traderTask.setBaseUnit(registerGoods.getBaseUnit());
        traderTask.setCashUnit(registerGoods.getCashUnit());
        waitTaskList.put(taskId, traderTask);
        logger.info("live Task TraderTask task: {}", traderTask);

        Task resultTask = ecsUtils.syncRun(traderTask);
        registerGoods.setTaskEcsId(resultTask.getTaskArn());
        try {
            int changeRow = sqlSession.update("goods.updateTaskEcsId", registerGoods);
            if (changeRow != 1) {
                logger.error("[FAIL] sql execute. changeRow: {}", changeRow);
                throw new OperationException("[FAIL] sql execute. changeRow: {}" + changeRow);
            }
        } catch (Exception e) {
            logger.error("[FAIL] sql execute.", e);
            throw new OperationException("[FAIL] sql execute.");
        }
        return resultTask;
    }

    public Task liveTaskStop(TraderTask traderTask) {
        if (!identityService.isManager(traderTask.getUserId())) {
            logger.info("접근권환 없는 사용자 요청. 사용자: {}", traderTask.getUserId());
            throw new AuthenticationException();
        }
        Goods registerGoods = goodsService.getGoods(traderTask.getGoodsId());
        if (traderTask.getGoodsId() == null || registerGoods == null) {
            throw new ParameterException("goodsId");
        }
        if (registerGoods.getTaskEcsId() == null || waitTaskList.get(registerGoods.getId()) != null) {
            throw new OperationException("It is already in progress.");
        }

        Task task = ecsUtils.stopTask(registerGoods.getTaskEcsId());

        try {
            registerGoods.setTaskEcsId(null);
            int changeRow = sqlSession.update("goods.updateTaskEcsId", registerGoods);
            if (changeRow != 1) {
                logger.error("[FAIL] sql execute. changeRow: {}", changeRow);
                throw new OperationException("[FAIL] sql execute. changeRow: {}" + changeRow);
            }
        } catch (Exception e) {
            logger.error("[FAIL] sql execute.");
            throw new OperationException("[FAIL] sql execute.");
        }
        logger.debug("[{}] ecs stop.", registerGoods.getId());

        return task;
    }

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
        TraderTask traderTask = new TraderTask();
        traderTask.setId(task_id);
        traderTask.setUserId("joonwoo");
        traderTask.setStrategyId(18);
        waitTaskList.put(task_id, traderTask);
    }

}