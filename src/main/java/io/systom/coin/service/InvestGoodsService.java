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

import java.util.Date;
import java.util.List;

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
    @Autowired
    private InvitationService invitationService;
    @Autowired
    private InvoiceService invoiceService;

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
    @Value("${invest.initCommission}")
    private float initCommission;
    @Value("${invest.maxFriendsSaleCount}")
    private int maxFriendsSaleCount;



    public InvestGoods registrationInvestor(InvestGoods investor) {
        Goods registerGoods = goodsService.getGoods(investor.getGoodsId());
        if (registerGoods == null || !registerGoods.getDisplay()) {
            throw new RequestException("invalid goods");
        }
        else if (investor.getInvestCash() == null
                || investor.getInvestCash().floatValue() <= 0) {
            throw new ParameterException("invest amount");
        }
        if (!investor.isPaper()) {
            ExchangeKey exchangeKey = new ExchangeKey();
            exchangeKey.setId(investor.getExchangeKeyId());
            exchangeKey.setUserId(investor.getUserId());
            exchangeKey = exchangeService.selectExchangeKey(exchangeKey);
            if (exchangeKey == null
                    || !registerGoods.getExchange().toLowerCase().equals(exchangeKey.getExchange().toLowerCase())) {
                throw new RequestException("invalid exchange key");
            }
        } else {
            investor.setExchangeKeyId(-1);
        }
        investor.setFinished(false);
        InvestGoods investGoods = findInvestGoodsByUser(investor.getGoodsId(), investor.getUserId());
        if (investGoods != null) {
            throw new RequestException("goods that are already investing");
        }

        try {
            int changeRow = sqlSession.insert("investGoods.registrationInvestor", investor);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
            changeRow = goodsService.updateChangeUsers(investor.getGoodsId());
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
        registerGoods.setPaper(investGoods.isPaper());
        registerGoods.setEndTime(investGoods.getEndTime());
        registerGoods.setFinished(investGoods.isFinished());
        List<TraderTaskResult.Result.Trade> tradeHistory = tradeService.getTradeHistory(investId);
        registerGoods.setTradeHistory(tradeHistory);
        PerformanceSummary performanceSummary = performanceService.getPerformanceSummary(investId);
        InvestGoods registerInvestGoods = getInvestGoods(investId);
        registerGoods.setInvestCash(registerInvestGoods.getInvestCash());
        registerGoods.setInvestTime(investGoods.getCreateTime());
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
        try {
            int changeRow = sqlSession.update("investGoods.deleteInvestGoods", investGoods.getId());
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
            changeRow = goodsService.updateChangeUsers(investGoods.getGoodsId());
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException();
        }
        userMonthInvestService.updateMonthlyCalculation(userId);

        invoiceService.createGoodsInvoice(investGoods);

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

    public InvestGoodsCommission calculateCommission(Integer investId) {
        InvestGoodsCommission investGoodsCommission = new InvestGoodsCommission();
        investGoodsCommission.setId(investId);
        InvestGoods investGoodsInfo = getInvestGoods(investId);
        Goods registerGoods = goodsService.getGoods(investGoodsInfo.getGoodsId());
        PerformanceSummary perf = performanceService.getPerformanceSummary(investId);
        investGoodsCommission.setPaper(investGoodsInfo.isPaper());
        investGoodsCommission.setGoodsId(investGoodsInfo.getGoodsId());
        investGoodsCommission.setUserId(investGoodsInfo.getUserId());
        investGoodsCommission.setCreateTime(investGoodsInfo.getCreateTime());
        investGoodsCommission.setEndTime(new Date());
        investGoodsCommission.setInitCash(investGoodsInfo.getInvestCash());
        investGoodsCommission.setEntity(perf.getEquity());
        investGoodsCommission.setReturns(perf.getReturns());
        investGoodsCommission.setReturnPct(perf.getReturnsPct());
        investGoodsCommission.setCashUnit(registerGoods.getCashUnit());
        investGoodsCommission.setCommUnit(registerGoods.getCashUnit());

        int friendCount = invitationService.getFriendsCount(investGoodsInfo.getUserId());
        float discount  = friendCount > maxFriendsSaleCount ? 5 : friendCount;
        float commissionRate = ((initCommission - discount) / 100);
        investGoodsCommission.setCommissionPct(commissionRate * 100);
        investGoodsCommission.setCommission(0);

//        1. 수익 0이상 처음 40%에서 친구초대시 한명당 1% 할인 최대 5%
        if (perf.getEquity() > perf.getInitCash()) {
            float paymentPrice = (perf.getEquity() - perf.getInitCash()) * commissionRate;
            investGoodsCommission.setCommission(paymentPrice);
        }
        investGoodsCommission.setTotalReturns((perf.getEquity() - perf.getInitCash()) - investGoodsCommission.getCommission());
        logger.info("투자 종료 수수료 계산: {}", investGoodsCommission);
        return investGoodsCommission;
    }
}