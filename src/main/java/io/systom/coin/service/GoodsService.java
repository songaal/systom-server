package io.systom.coin.service;

import com.amazonaws.services.ecs.model.Task;
import com.google.gson.Gson;
import io.systom.coin.exception.AuthenticationException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.exception.ParameterException;
import io.systom.coin.exception.RequestException;
import io.systom.coin.model.*;
import io.systom.coin.utils.EcsUtils;
import io.systom.coin.utils.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

import static io.systom.coin.utils.DateUtils.formatDate;

/*
 * create joonwoo 2018. 7. 3.
 * 
 */
@Service
public class GoodsService {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(GoodsService.class);

    @Autowired
    private SqlSession sqlSession;
    @Autowired
    private StrategyDeployService strategyDeployService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private ExchangeService exchangeService;
    @Autowired
    private InvestGoodsService investGoodsService;
    @Autowired
    private EcsUtils ecsUtils;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private UserMonthInvestService userMonthInvestService;
    @Autowired
    private PerformanceService performanceService;

//    public static final String DATE_FORMAT = "yyyyMMdd";
//    public static final String TIME_FORMAT = "HHmmss";

    @Transactional
    public Goods registerGoods(Goods target){

        if (!identityService.isManager(target.getAuthorId())){
            throw new AuthenticationException();
        } else if (!isGoodsValidation(target)) {
            throw new ParameterException("Require");
        }

        target.setExchange(target.getExchange().toLowerCase());
        target.setCashUnit(target.getCashUnit().toUpperCase());
        target.setBaseUnit(target.getBaseUnit().toUpperCase());
        target.setCoinUnit(target.getCoinUnit().toUpperCase());

        GoodsTestResult testResult = new GoodsTestResult();
        List<MonthlyReturn> testMonthlyReturnList = generatorTestMonthlyReturns(target.getTestStart(), target.getTestEnd());
        testResult.setTestMonthlyReturnList(testMonthlyReturnList);
        testResult.setTestMaxMonthlyPct(0);
        testResult.setTestMinMonthlyPct(0);
        testResult.setTradeHistory(new ArrayList<>());

        target.setTestResult(new Gson().toJson(testResult));
        try {
            int changeRow = sqlSession.insert("goods.registerGoods", target);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }

        return getGoods(target.getId());
    }

    public Goods getGoods(Integer goodsId) {
        return getGoods(goodsId, null);
    }
    public Goods getGoods(Integer goodsId, String userId) {
        Goods searchGoods = new Goods();
        searchGoods.setId(goodsId);
        searchGoods.setUserId(userId);
        Goods registerGoods;

        try {
            registerGoods = sqlSession.selectOne("goods.getGoods", searchGoods);
            if (registerGoods == null) {
                throw new ParameterException("goodsId");
            }

            if (registerGoods.getPublicInvestId() != null) {
                registerGoods.setPublicTradeHistory(tradeService.getTradeHistory(registerGoods.getPublicInvestId()));
                registerGoods.setPublicMonthlyReturnsPct(userMonthInvestService.getDailyToMonthlyList(registerGoods.getPublicInvestId()));
                PerformanceSummary perf = performanceService.getPerformanceSummary(registerGoods.getPublicInvestId());
                registerGoods.setPublicReturnsPct(perf.getReturnsPct());
                registerGoods.setPublicMdd(perf.getMdd());
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }

        return registerGoods;
    }

    public boolean isOrderExecute(Integer goodsId) {
        boolean isExecuting = sqlSession.selectOne("goods.isOrderExecute", goodsId);
        logger.debug("isExecuting: {}", isExecuting);
        return isExecuting;
    }

    public List<Goods> retrieveGoodsList(Goods searchGoods) {
        try {
            return sqlSession.selectList("goods.retrieveGoodsList", searchGoods);
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

    public Goods updateGoodsHide(Integer id, String userId) {
        Goods registerGoods = getGoods(id);
        if (registerGoods == null) {
            throw new ParameterException("GoodsId");
        } else if (!identityService.isManager(userId)) {
            throw new AuthenticationException();
        }
        try {
            int changeRow = sqlSession.update("goods.updateGoodsHide", registerGoods.getId());
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
        return getGoods(id);
    }

    public Goods updateGoodsShow(Integer id, String userId) {
        Goods registerGoods = getGoods(id);
        if (registerGoods == null) {
            throw new ParameterException("GoodsId");
        } else if (!identityService.isManager(userId)) {
            throw new AuthenticationException();
        }
        try {
            int changeRow = sqlSession.update("goods.updateGoodsShow", registerGoods.getId());
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
        return getGoods(id);
    }

    @Transactional
    public Goods deleteGoods(Integer id, String userId) {
        Goods registerGoods = getGoods(id);
        if (registerGoods == null) {
            throw new ParameterException("GoodsId");
        }
        if (!identityService.isManager(userId)) {
            throw new AuthenticationException();
        }

        List<InvestGoods> registerInvestGoodsList = investGoodsService.findInvestGoodsByUserList(id);
        if (registerInvestGoodsList == null || registerInvestGoodsList.size() >= 1) {
            throw new RequestException("not empty");
        }

        try {
            int changeRow = 0;
            changeRow = sqlSession.delete("goods.deleteGoods", id);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }

        return registerGoods;
    }

    @Transactional
    public Goods updateGoods(Goods target) {
        Goods registerGoods = getGoods(target.getId());
        if (registerGoods == null) {
            throw new ParameterException("inValid GoodsId");
        } else if (!identityService.isManager(target.getAuthorId())) {
            throw new AuthenticationException();
        } else if (!isGoodsValidation(target)) {
            throw new ParameterException("Require");
        }

        target.setExchange(target.getExchange().toLowerCase());
        target.setCashUnit(target.getCashUnit().toUpperCase());
        target.setBaseUnit(target.getBaseUnit().toUpperCase());
        target.setCoinUnit(target.getCoinUnit().toUpperCase());

        try {
            int changeRow = sqlSession.update("goods.updateGoods", target);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute");
        }

        return getGoods(target.getId());
    }

    protected boolean isGoodsValidation(Goods target) {
        if (target == null || target.getStrategyId() == null || target.getVersion() == null) {
            logger.debug("Invalid strategy id, version");
            return false;
        } else if (StringUtils.isEmpty(target.getAuthorId()) || StringUtils.isBlank(target.getAuthorId())) {
            logger.debug("Invalid AuthorId");
            return false;
        } else if (StringUtils.isEmpty(target.getCoinUnit()) || StringUtils.isBlank(target.getCoinUnit())) {
            logger.debug("Invalid coin");
            return false;
        } else if (StringUtils.isEmpty(target.getName()) || StringUtils.isBlank(target.getName())) {
            logger.debug("Invalid name");
            return false;
        } else if (StringUtils.isEmpty(target.getDescription()) || StringUtils.isBlank(target.getDescription())) {
            logger.debug("Invalid description");
            return false;
        } else if (target.getCash() == null) {
            logger.debug("Invalid amount");
            return false;
        } else if (StringUtils.isEmpty(target.getCashUnit()) || StringUtils.isBlank(target.getCashUnit())) {
            logger.debug("Invalid currency");
            return false;
        } else if (target.getTestStart() == null || target.getTestEnd() == null
                || String.valueOf(target.getTestStart()).length() != 8
                || String.valueOf(target.getTestEnd()).length() != 8
                || Integer.parseInt(target.getTestStart()) > Integer.parseInt(target.getTestEnd())) {
            logger.debug("Invalid BackTestDate");
//            백테스트 시작일은 모집 종료일보다 클 수 없음.
            return false;
        }

        try {
            StrategyDeployVersion deployVersion = new StrategyDeployVersion();
            deployVersion.setId(target.getStrategyId());
            deployVersion.setVersion(target.getVersion());
            deployVersion.setUserId(target.getAuthorId());
            deployVersion = strategyDeployService.getDeployVersion(deployVersion);
            if (deployVersion == null){
                return false;
            }
        } catch (OperationException e) {
            logger.error("", e);
            return false;
        } catch (ParameterException e) {
            return false;
        }

        return true;
    }

    protected List<MonthlyReturn> generatorTestMonthlyReturns(String testStart, String testEnd) {
        int startYear = Integer.parseInt(testStart.substring(0, 4));
        int startMonth = Integer.parseInt(testStart.substring(4, 6));
        int endYear = Integer.parseInt(testEnd.substring(0, 4));
        int endMonth = Integer.parseInt(testEnd.substring(4, 6));

        List<MonthlyReturn> testMonthlyReturnList = new ArrayList<>();
        boolean isSameYear = true;
        for(int y=startYear; y <= endYear; y++) {
            if (y < endYear) {
                for (int m = startMonth; m <= 12; m++) {
                    testMonthlyReturnList.add(new MonthlyReturn(formatDate(y, m, null), 0));
                }
                isSameYear = false;
            } else if (y == endYear) {
                int sm = startMonth;
                if (!isSameYear) {
                    sm = 1;
                }
                for (int m = sm; m <= endMonth; m++) {
                    testMonthlyReturnList.add(new MonthlyReturn(formatDate(y, m, null), 0));
                }
            }
        }
        return testMonthlyReturnList;
    }

    public Goods resetGoodsTestResult(Integer id) {
        Goods registerGoods = getGoods(id);
        GoodsTestResult testResult = new GoodsTestResult();
        List<MonthlyReturn> testMonthlyReturnList = generatorTestMonthlyReturns(registerGoods.getTestStart(), registerGoods.getTestEnd());
        testResult.setTestMonthlyReturnList(testMonthlyReturnList);
        testResult.setTestMaxMonthlyPct(0);
        testResult.setTestMinMonthlyPct(0);
        testResult.setTradeHistory(new ArrayList<>());
        registerGoods.setTestResult(new Gson().toJson(testResult));
        return updateGoods(registerGoods);
    }

    public List<String> selectInvestUserId(Integer id) {
        return sqlSession.selectList("goods.selectInvestUserId", id);
    }

    public List<Goods> selectGoodsIdList(String column, String val) {
        Map<String, String> param = new HashMap<>();
        param.put("column", column);
        param.put("val", val);
        return sqlSession.selectList("goods.selectGoodsIdList", param);
    }

    public int updateChangeUsers(int goodsId) {
        return sqlSession.update("goods.updateChangeUsers", goodsId);
    }

    public int updatePublicInvestId(int goodsId, int publicInvestId) {
        Map<String, Object> param = new HashMap<>();
        param.put("id", goodsId);
        param.put("publicInvestId", publicInvestId);
        return sqlSession.update("goods.updatePublicInvestId", param);
    }

    public List<String> getRunningTaskList () {
        return ecsUtils.getRunningTaskList();
    }

    public Task getDescribeTasks(String taskEcsId) {
        return ecsUtils.getDescribeTasks(taskEcsId);
    }

    public String order (Integer goodsId, Float coinWeight, String coinAction, Float baseWeight, String baseAction, String message, String userId) {
        if (!identityService.isManager(userId)) {
            throw new AuthenticationException();
        }

        Goods goods = getGoods(goodsId);
        if (goods == null || goods.getTaskEcsId() == null) {
            throw new OperationException("TASK ECS ID IS NULL");
        }
        boolean isDual = !goods.getCashUnit().equals(goods.getBaseUnit());

        String symbol = String.format("%s/%s", goods.getCoinUnit(), goods.getBaseUnit());
        CoinSignal coinSignal = new CoinSignal();
        coinSignal.setType("SIGNAL");
        coinSignal.setAction(coinAction);
        coinSignal.setTime((int) new Date().getTime());
        coinSignal.setSymbol(symbol);
        coinSignal.setWeight(coinWeight);
        CoinSignal.Reason coinReason = new CoinSignal.Reason();
        coinReason.setAuthor(userId);
        coinReason.setMessage(message);
        coinSignal.setReason(coinReason);

        ResponseEntity<String> response;
        if (isDual) {
//            dual
            String baseSymbol = String.format("%s/%s", goods.getBaseUnit(), goods.getCashUnit());
            CoinSignal baseSignal = new CoinSignal();
            baseSignal.setType("SIGNAL");
            baseSignal.setAction(baseAction);
            baseSignal.setTime((int) new Date().getTime());
            baseSignal.setSymbol(baseSymbol);
            baseSignal.setWeight(baseWeight);
            CoinSignal.Reason baseReason = new CoinSignal.Reason();
            baseReason.setAuthor(userId);
            baseReason.setMessage(message);
            baseSignal.setReason(baseReason);

            DualSignal dualSignal = new DualSignal();
            dualSignal.setType("SIGNAL");
            dualSignal.setTime(new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(new Date()));
            dualSignal.setCoin_signal(coinSignal);
            dualSignal.setBase_signal(baseSignal);
            response = ecsUtils.executorSignal(goods.getTaskEcsId(), dualSignal);
        } else {
//             single
            response = ecsUtils.executorSignal(goods.getTaskEcsId(), coinSignal);
        }

        logger.debug("Order result >> {}", response.getBody());
        return response.getBody();
    }

}