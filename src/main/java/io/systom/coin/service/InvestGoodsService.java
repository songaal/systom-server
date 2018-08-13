package io.systom.coin.service;

import io.systom.coin.exception.AuthenticationException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.exception.ParameterException;
import io.systom.coin.exception.RequestException;
import io.systom.coin.model.*;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static io.systom.coin.service.GoodsService.DATE_FORMAT;
import static io.systom.coin.service.GoodsService.TIME_FORMAT;

/*
 * create joonwoo 2018. 7. 3.
 * 
 */
@Service
public class InvestGoodsService {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(InvestGoodsService.class);

    @Autowired
    private SqlSession sqlSession;
    @Autowired
    private StrategyDeployService strategyDeployService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private ExchangeService exchangeService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private PerformanceService performanceService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private UserMonthInvestService userMonthInvestService;

    @Value("${invest.start.hour}")
    private String startHour;
    @Value("${invest.start.minute}")
    private String startMinute;
    @Value("${invest.start.second}")
    private String startSecond;
    @Value("${invest.end.hour}")
    private String endHour;
    @Value("${invest.end.minute}")
    private String endMinute;
    @Value("${invest.end.second}")
    private String endSecond;

    public InvestGoods registrationInvestor(InvestGoods investor) {
        Goods registerGoods = goodsService.getGoods(investor.getGoodsId());
        long nowTs = Integer.parseInt(new SimpleDateFormat(DATE_FORMAT + TIME_FORMAT).format(new Date()));
        if (registerGoods == null || !registerGoods.getDisplay()) {
            throw new RequestException("invalid goods");
        } else if (Integer.parseInt(String.format("%s%s%s%s", registerGoods.getCollectStart(), startHour, startMinute, startSecond)) > nowTs
                ||Integer.parseInt(String.format("%s%s%s%s", registerGoods.getCollectEnd(), endHour, endMinute, endSecond)) < nowTs) {
            throw new RequestException("not collect invest goods");
        } else if(investor.getInvestCash() == null
                || investor.getInvestCash().floatValue() <= 0) {
            throw new ParameterException("invest amount");
        }
        ExchangeKey exchangeKey = new ExchangeKey();
        exchangeKey.setId(investor.getExchangeKeyId());
        exchangeKey.setUserId(investor.getUserId());
        exchangeKey = exchangeService.selectExchangeKey(exchangeKey);
        if (exchangeKey == null
                || !registerGoods.getExchange().toLowerCase().equals(exchangeKey.getExchange().toLowerCase())) {
            throw new RequestException("invalid exchange key");
        }

        InvestGoods investGoods = findInvestGoodsByUser(investor.getGoodsId(), investor.getUserId());
        if (investGoods != null) {
            throw new RequestException("goods that are already investing");
        }

        try {
            int changeRow = sqlSession.insert("investGoods.registrationInvestor", investor);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }

        PerformanceSummary performanceSummary = new PerformanceSummary();
        performanceSummary.setInvestId(investor.getId());
        performanceSummary.setCash(investor.getInvestCash());
        performanceSummary.setInitCash(investor.getInvestCash());
        performanceSummary.setEquity(investor.getInvestCash());
        performanceService.insertPerformanceSummary(performanceSummary);
        performanceService.insertTradeStat(investor.getId());
        userMonthInvestService.updateMonthlyCalculation(investor.getUserId());
        return getInvestGoods(investor.getId());
    }

    public InvestGoods getInvestGoods(Integer investId) {
        try {
            return sqlSession.selectOne("investGoods.getInvestGoods", investId);
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

    public Goods getInvestGoodsDetail(Integer investId, String userId) {
        InvestGoods investGoods = getInvestGoods(investId);
        if (investGoods == null) {
            throw new ParameterException("investId");
        } else if (!investGoods.getUserId().equals(userId)) {
            throw new AuthenticationException();
        }

        Goods registerGoods = goodsService.getGoods(investGoods.getGoodsId(), userId);
        List<TraderTaskResult.Result.Trade> tradeHistory = tradeService.getTradeHistory(investId);
        registerGoods.setTradeHistory(tradeHistory);
        PerformanceSummary performanceSummary = performanceService.getPerformanceSummary(investId);
        InvestGoods registerInvestGoods = getInvestGoods(investId);
        registerGoods.setInvestCash(registerInvestGoods.getInvestCash());
        registerGoods.setPerformanceSummary(performanceSummary);
        List<PerformanceDaily> performanceDailyList = performanceService.getPerformanceDailyList(investId);
        registerGoods.setPerformanceDaily(performanceDailyList);
        registerGoods.setTradeStat(performanceService.getTradeStat(investId));
        return registerGoods;
    }

    public InvestGoods removeInvestor(int investId, String userId) {
        InvestGoods investGoods = getInvestGoods(investId);
        if (investGoods == null){
            throw new ParameterException("investId");
        } else if (!investGoods.getUserId().equals(userId)) {
            throw new AuthenticationException();
        }

//        Goods registerGoods = goodsService.getGoods(investGoods.getGoodsId());
//        int nowTime = Integer.parseInt(new SimpleDateFormat(DATE_FORMAT).format(new Date()));
//        if (Integer.parseInt(registerGoods.getCollectStart()) > nowTime
//                || Integer.parseInt(registerGoods.getCollectEnd()) < nowTime) {
//            throw new RequestException("not collect invest goods");
//        }

//        performanceService.deletePerformanceSummary(investId);
        performanceService.deleteTradeStat(investId);
        try {
            int changeRow = sqlSession.delete("investGoods.deleteInvestGoods", investGoods.getId());
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException();
        }
        userMonthInvestService.updateMonthlyCalculation(userId);
        return investGoods;
    }

    public List<Goods> retrieveInvestGoods(String userId) {
        try {
            return sqlSession.selectList("investGoods.retrieveInvestGoods", userId);
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

    public List<InvestGoods> findInvestGoodsByUserList(Integer goodsId) {
        InvestGoods investGoods = new InvestGoods();
        investGoods.setGoodsId(goodsId);
        try {
            return sqlSession.selectList("investGoods.findInvestGoodsByUserList", investGoods);
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

    public InvestGoods findInvestGoodsByUser(Integer goodsId, String userId) {
        InvestGoods investGoods = new InvestGoods();
        investGoods.setGoodsId(goodsId);
        investGoods.setUserId(userId);
        try {
            return sqlSession.selectOne("investGoods.findInvestGoodsByUser", investGoods);
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }
}