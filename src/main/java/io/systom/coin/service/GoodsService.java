package io.systom.coin.service;

import com.google.gson.Gson;
import io.systom.coin.config.TradeConfig;
import io.systom.coin.exception.AuthenticationException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.exception.ParameterException;
import io.systom.coin.exception.RequestException;
import io.systom.coin.model.*;
import io.systom.coin.utils.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private TradeConfig tradeConfig;
    @Autowired
    private ExchangeService exchangeService;
    @Autowired
    private InvestGoodsService investGoodsService;
    @Autowired
    private TradeService tradeService;

    public static final String DATE_FORMAT = "yyyyMMdd";

    public static final String BOT_USER_ID = "0";
    public static final float BOT_INVEST_CASH = 0f;
    public static final int BOT_EXCHANGE_KEY_ID = 0;

    @Transactional
    public Goods registerGoods(Goods target){

        if (!identityService.isManager(target.getUserId())){
            throw new AuthenticationException();
        } else if (!isGoodsValidation(target)) {
            throw new ParameterException("Require");
        }

        target.setExchange(target.getExchange().toLowerCase());
        target.setCashUnit(target.getCashUnit().toLowerCase());
        target.setBaseUnit(target.getBaseUnit().toLowerCase());
        target.setCoinUnit(target.getCoinUnit().toLowerCase());

        List<TestMonthlyReturn> testMonthlyReturnList = generatorTestMonthlyReturns(target.getTestStart(), target.getTestEnd());
        target.setTestReturnPct(0f);
        target.setTestMonthlyReturn(new Gson().toJson(testMonthlyReturnList));

        try {
            int changeRow = sqlSession.insert("goods.registerGoods", target);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }

        InvestGoods botInvestGoods = new InvestGoods();
        botInvestGoods.setGoodsId(target.getId());
        botInvestGoods.setUserId(BOT_USER_ID);
        botInvestGoods.setInvestCash(BOT_INVEST_CASH);
        botInvestGoods.setExchangeKeyId(BOT_EXCHANGE_KEY_ID);
        investGoodsService.registrationInvestor(botInvestGoods);

        return getGoods(target.getId());
    }

    public Goods getGoods(Integer goodsId) {
        return getGoods(goodsId, null);
    }
    public Goods getGoods(Integer goodsId, String userId) {
        Goods searchGoods = new Goods();
        searchGoods.setId(goodsId);
        searchGoods.setUserId(userId);
        Goods registerGoods = null;

        try {
            registerGoods = sqlSession.selectOne("goods.getGoods", searchGoods);
            if (registerGoods == null) {
                throw new ParameterException("goodsId");
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }

        InvestGoods botInvestGoods = investGoodsService.findInvestGoodsByUser(registerGoods.getId(), BOT_USER_ID);
        if (botInvestGoods != null) {
            List<Trade> tradeHistory = tradeService.getTradeHistory(botInvestGoods.getId());
            registerGoods.setTradeHistory(tradeHistory);
        }

        return registerGoods;
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
        if (registerInvestGoodsList == null || registerInvestGoodsList.size() > 1) {
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
        int nowTime = Integer.parseInt(new SimpleDateFormat(DATE_FORMAT).format(new Date()));
        if (registerGoods == null) {
            throw new ParameterException("inValid GoodsId");
        } else if (!registerGoods.getUserId().equals(target.getUserId())) {
            throw new AuthenticationException();
        } else if (!isGoodsValidation(target)) {
            throw new ParameterException("Require");
        }

        if (!registerGoods.getTestStart().equals(target.getTestStart())
                ||!registerGoods.getTestEnd().equals(target.getTestEnd())) {
//            백테스트 시간이 변경되면 기존 백테스트 전체 삭제
            List<TestMonthlyReturn> testMonthlyReturnList = generatorTestMonthlyReturns(target.getTestStart(), target.getTestEnd());
            target.setTestMonthlyReturn(new Gson().toJson(testMonthlyReturnList));
            target.setTestReturnPct(0f);
            InvestGoods botInvestGoods = investGoodsService.findInvestGoodsByUser(registerGoods.getId(), BOT_USER_ID);
            tradeService.deleteTradeHistory(botInvestGoods.getId());
        }

        target.setExchange(target.getExchange().toLowerCase());
        target.setCashUnit(target.getCashUnit().toLowerCase());
        target.setBaseUnit(target.getBaseUnit().toLowerCase());
        target.setCoinUnit(target.getCoinUnit().toLowerCase());

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
            logger.debug("Invalid id, version");
            return false;
        } else if (StringUtils.isEmpty(target.getUserId()) || StringUtils.isBlank(target.getUserId())) {
            logger.debug("Invalid userId");
            return false;
        } else if (StringUtils.isEmpty(target.getExchange()) || StringUtils.isBlank(target.getExchange())
                || !tradeConfig.getLiveExchange().contains(target.getExchange().toLowerCase())) {
            logger.debug("Invalid exchange");
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
        } else if (target.getRecruitStart() == null || target.getRecruitEnd() == null
                || String.valueOf(target.getRecruitStart()).length() != 8
                || String.valueOf(target.getRecruitEnd()).length() != 8
                || Integer.parseInt(target.getRecruitStart()) > Integer.parseInt(target.getRecruitEnd())) {
//            모집 시작일은 모집 종료일보다 클 수 없음.
            logger.debug("Invalid RecruitDate");
            return false;
        } else if (target.getInvestStart() == null || target.getInvestEnd() == null
                || String.valueOf(target.getInvestStart()).length() != 8
                || String.valueOf(target.getInvestEnd()).length() != 8
                || Integer.parseInt(target.getInvestStart()) > Integer.parseInt(target.getInvestEnd())) {
            logger.debug("Invalid investDate");
//            투자 시작일은 모집 종료일보다 클 수 없음.
            return false;
        } else if (target.getTestStart() == null || target.getTestEnd() == null
                || String.valueOf(target.getTestStart()).length() != 8
                || String.valueOf(target.getTestEnd()).length() != 8
                || Integer.parseInt(target.getTestStart()) > Integer.parseInt(target.getTestEnd())) {
            logger.debug("Invalid BackTestDate");
//            백테스트 시작일은 모집 종료일보다 클 수 없음.
            return false;
        } else if (Integer.parseInt(target.getRecruitEnd()) > Integer.parseInt(target.getInvestStart())) {
            logger.debug("Invalid RecruitEnd >= InvestStart");
//            모집 종료일은 투자 시작일보다 클 수 없음.
            return false;
        }

        try {
            StrategyDeployVersion deployVersion = new StrategyDeployVersion();
            deployVersion.setId(target.getStrategyId());
            deployVersion.setVersion(target.getVersion());
            deployVersion.setUserId(target.getUserId());
            deployVersion = strategyDeployService.getDeployVersion(deployVersion);
            if (deployVersion == null){
                return false;
            } else if (!deployVersion.getUserId().equals(target.getUserId())) {
                throw new AuthenticationException();
            }

        } catch (OperationException e) {
            logger.error("", e);
            return false;
        } catch (ParameterException e) {
            return false;
        }

        return true;
    }

    protected List<TestMonthlyReturn> generatorTestMonthlyReturns(String testStart, String testEnd) {
        int startYear = Integer.parseInt(testStart.substring(0, 4));
        int startMonth = Integer.parseInt(testStart.substring(4, 6));
        int endYear = Integer.parseInt(testEnd.substring(0, 4));
        int endMonth = Integer.parseInt(testEnd.substring(4, 6));

        List<TestMonthlyReturn> testMonthlyReturnList = new ArrayList<>();
        boolean isSameYear = true;
        for(int y=startYear; y <= endYear; y++) {
            if (y < endYear) {
                for (int m = startMonth; m <= 12; m++) {
                    testMonthlyReturnList.add(new TestMonthlyReturn(formatDate(y, m, null), 0));
                }
                isSameYear = false;
            } else if (y == endYear) {
                int sm = startMonth;
                if (!isSameYear) {
                    sm = 1;
                }
                for (int m = sm; m <= endMonth; m++) {
                    testMonthlyReturnList.add(new TestMonthlyReturn(formatDate(y, m, null), 0));
                }
            }
        }
        return testMonthlyReturnList;
    }

}