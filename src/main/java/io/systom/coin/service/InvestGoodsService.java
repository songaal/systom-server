package io.systom.coin.service;

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
    private TradeConfig tradeConfig;
    @Autowired
    private ExchangeService exchangeService;

    public static final String BOT_USER_ID = "0";
    public static final Integer BOT_EXCHANGE_KEY_ID = 0;
    public static final Float BOT_INVEST_AMOUNT = 1f;

    @Transactional
    public Goods registerInvestGoods(Goods target){

        if (!identityService.isManager(target.getUserId())){
            throw new AuthenticationException();
        } else if (!isGoodsValidation(target)) {
            throw new ParameterException("Require");
        }

        target.setMaxAmount(calculationMaxAmount(target.getAmount()));
        target.setMinAmount(calculationMinAmount(target.getAmount()));
        target.setCurrency(target.getCurrency().toLowerCase());
        target.setExchange(target.getExchange().toLowerCase());
        target.setCoin(target.getCoin().toLowerCase());

        try {
//            상품등록
            int changeRow = sqlSession.insert("goods.registerGoods", target);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
//            상품 구매
            InvestGoods investBot = new InvestGoods();
            investBot.setGoodsId(target.getId());
            investBot.setUserId(BOT_USER_ID);
            investBot.setAmount(BOT_INVEST_AMOUNT);
            investBot.setExchangeKeyId(BOT_EXCHANGE_KEY_ID);

            addInvestors(investBot);

//            퍼포먼스 row 추가
            addBlankPerformance(investBot.getId(), BOT_INVEST_AMOUNT);
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
        return getGoods(target.getId(), BOT_USER_ID);
    }

    protected float calculationMaxAmount(float amount) {
        return amount / 2;
    }
    protected float calculationMinAmount(float amount) {
        return amount / 100;
    }

    protected Integer addInvestors(InvestGoods investor) {
        try {
            int changeRow = sqlSession.insert("goods.addInvestors", investor);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
        return investor.getId();
    }

    protected TaskResult.Result addBlankPerformance(Integer investId, float amount) {
        InvestGoods paperBot = new InvestGoods();
        paperBot.setId(investId);
        paperBot.setAmount(amount);
        try {
            int changeRow = sqlSession.insert("goods.addBlankPerformance", paperBot);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
        return getGoodsPerformance(investId);
    }

    private Goods getGoods(Integer goodsId, String userId) {
        Goods registerGoods = getGoodsMeta(goodsId);
        InvestGoods registerInvestGoods = findInvestIdByUser(registerGoods.getId(), userId);
        TaskResult.Result performance = getGoodsPerformance(registerInvestGoods.getId());
        performance.setTradeHistory(retrieveTradeHistory(registerInvestGoods.getId()));
        registerGoods.setPerformance(performance);
        return registerGoods;
    }

    public Goods getRecruitGoods(Integer goodsId) {
        Goods registerGoods = getGoodsMeta(goodsId);
        long nowTime = System.currentTimeMillis();
        if (registerGoods != null && registerGoods.getDisplay()
                && nowTime >= registerGoods.getRecruitStart()
                && nowTime <= registerGoods.getRecruitEnd()) {
            InvestGoods registerInvestGoods = findInvestIdByUser(registerGoods.getId(), BOT_USER_ID);
            TaskResult.Result performance = getGoodsPerformance(registerInvestGoods.getId());
            performance.setTradeHistory(retrieveTradeHistory(registerInvestGoods.getId()));
            registerGoods.setPerformance(performance);
        }
        return registerGoods;
    }

    public List<InvestGoods> findInvestIdByUserList(Integer goodsId) {
        InvestGoods investGoods = new InvestGoods();
        investGoods.setGoodsId(goodsId);
        try {
            return sqlSession.selectList("goods.findInvestIdByUserList", investGoods);
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }
    public InvestGoods findInvestIdByUser(Integer goodsId, String userId) {
        InvestGoods investGoods = new InvestGoods();
        investGoods.setGoodsId(goodsId);
        investGoods.setUserId(userId);
        try {
            return sqlSession.selectOne("goods.findInvestIdByUser", investGoods);
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

    public Goods getGoodsMeta(Integer goodsId) {
        try {
            return  sqlSession.selectOne("goods.getGoodsMeta", goodsId);
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

    public TaskResult.Result getGoodsPerformance(Integer investId) {
        try {
            return sqlSession.selectOne("goods.getGoodsPerformance", investId);
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

    public List<TaskResult.Result.Trade> retrieveTradeHistory(Integer investId) {
        try {
            return sqlSession.selectList("goods.retrieveTradeHistory", investId);
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

    public List<Goods> retrieveGoodsList(Goods searchGoods) {
        try {
            return sqlSession.selectList("goods.retrieveGoodsList", searchGoods);
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

    public Goods updateRecruitGoodsHide(Integer id, String userId) {
        Goods registerGoods = getGoods(id, BOT_USER_ID);
        if (registerGoods == null || registerGoods.getInvestStart() <= System.currentTimeMillis()) {
            throw new ParameterException("GoodsId");
        }
        if (!registerGoods.getUserId().equals(userId)) {
            throw new AuthenticationException();
        }
        try {
            int changeRow = sqlSession.update("goods.updateGoodsHide", registerGoods);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
        return getGoods(id, BOT_USER_ID);
    }

    public Goods updateRecruitGoodsShow(Integer id, String userId) {
        Goods registerGoods = getGoods(id, BOT_USER_ID);
        if (registerGoods == null || registerGoods.getInvestStart() <= System.currentTimeMillis()) {
            throw new ParameterException("GoodsId");
        }
        if (!registerGoods.getUserId().equals(userId)) {
            throw new AuthenticationException();
        }
        try {
            int changeRow = sqlSession.update("goods.updateGoodsShow", registerGoods);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
        return getGoods(id, BOT_USER_ID);
    }

    @Transactional
    public Goods deleteGoods(Integer id, String userId) {
        Goods registerGoods = getGoodsMeta(id);
        if (registerGoods == null) {
            throw new ParameterException("GoodsId");
        }
        if (!registerGoods.getUserId().equals(userId)) {
            throw new AuthenticationException();
        }
        List<InvestGoods> registerInvestGoodsList = findInvestIdByUserList(id);
        if (registerInvestGoodsList.size() > 1) {
            // BOT 빼고 투자참여자 있을 경우 삭제 실패 처리.
            throw new RequestException("not empty");
        }
        try {
            int deleteSize = registerInvestGoodsList.size();
            for(int i=0; i < deleteSize; i++) {
                int investId = registerInvestGoodsList.get(i).getId();
                int changeRow = 0;
                changeRow = deleteTradeHistory(investId);
                logger.debug("DELETE STEP 1/5 >> TradeHistory rows: {}", changeRow);

                changeRow = deletePerformance(investId);
                logger.debug("DELETE STEP 2/5 >> Performance rows: {}", changeRow);

                changeRow = deleteValueHistory(investId);
                logger.debug("DELETE STEP 3/5 >> valueHistory rows: {}", changeRow);

                changeRow = deleteInvestGoods(investId);
                logger.debug("DELETE STEP 4/5 >> InvestGoods rows: {}", changeRow);

                changeRow = deleteGoodsMeta(id);
                logger.debug("DELETE STEP 5/5 >> deleteGoodsMeta rows: {}", changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }

        return registerGoods;
    }

    protected int deleteTradeHistory(Integer investId) {
        try {
            return sqlSession.delete("goods.deleteTradeHistory", investId);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException();
        }
    }

    protected int deletePerformance(Integer investId) {
        try {
            return sqlSession.delete("goods.deletePerformance", investId);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException();
        }
    }

    protected int deleteValueHistory(Integer investId) {
        try {
            return sqlSession.delete("goods.deleteValueHistory", investId);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException();
        }
    }

    protected int deleteInvestGoods(Integer investId) {
        try {
            return sqlSession.delete("goods.deleteInvestGoods", investId);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException();
        }
    }

    protected int deleteGoodsMeta(Integer investId) {
        try {
            return sqlSession.delete("goods.deleteGoodsMeta", investId);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException();
        }
    }

    @Transactional
    public Goods updateRecruitGoods(Goods target) {
        Goods registerGoods = getGoodsMeta(target.getId());
        if (registerGoods == null) {
            throw new ParameterException("inValid GoodsId");
        } else if (!registerGoods.getUserId().equals(target.getUserId())) {
            throw new AuthenticationException();
        } else if (registerGoods.getRecruitStart().longValue() > System.currentTimeMillis()
                || registerGoods.getRecruitEnd().longValue() < System.currentTimeMillis()) {
            throw new RequestException("not recruit invest goods");
        } else if (!isGoodsValidation(target)) {
            throw new ParameterException("Require");
        }

        if (!registerGoods.getBacktestStart().equals(target.getBacktestStart())
                ||!registerGoods.getBacktestEnd().equals(target.getBacktestEnd())) {
//            백테스트 시간이 변경되면 기존 백테스트 전체 삭제
            InvestGoods investGoods = findInvestIdByUser(registerGoods.getId(), BOT_USER_ID);
            deletePerformance(investGoods.getId());
            deleteValueHistory(investGoods.getId());
            deleteTradeHistory(investGoods.getId());
            addBlankPerformance(investGoods.getId(), BOT_INVEST_AMOUNT);
        }

        target.setMaxAmount(calculationMaxAmount(target.getAmount()));
        target.setMinAmount(calculationMinAmount(target.getAmount()));
        target.setCurrency(target.getCurrency().toLowerCase());
        target.setExchange(target.getExchange().toLowerCase());
        target.setCoin(target.getCoin().toLowerCase());

        try {
            int changeRow = sqlSession.update("goods.updateGoodsMeta", target);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute");
        }

        return getGoods(target.getId(), BOT_USER_ID);
    }

    @Transactional
    public InvestGoods registrationInvestor(InvestGoods investor) {
        Goods registerGoods = getGoodsMeta(investor.getGoodsId());
        long nowTs = System.currentTimeMillis();
        if (registerGoods == null || !registerGoods.getDisplay()) {
            throw new RequestException("invalid goods");
        } else if (registerGoods.getRecruitStart().longValue() > nowTs
                ||registerGoods.getRecruitEnd().longValue() < nowTs) {
            throw new RequestException("not recruit invest goods");
        } else if(investor.getAmount() == null
                || investor.getAmount().floatValue() <= 0) {
            throw new ParameterException("invest amount");
        }

        ExchangeKey exchangeKey = new ExchangeKey();
        exchangeKey.setId(investor.getExchangeKeyId());
        exchangeKey.setUserId(investor.getUserId());
        exchangeKey = exchangeService.selectExchangeKey(exchangeKey);
        if (exchangeKey == null || !registerGoods.getExchange().toLowerCase().equals(exchangeKey.getExchange().toLowerCase())) {
            throw new RequestException("invalid exchange key");
        }

        InvestGoods investGoods = findInvestIdByUser(investor.getGoodsId(), investor.getUserId());
        if (investGoods != null) {
            throw new RequestException("goods that are already investing");
        }

        addInvestors(investor);
        addBlankPerformance(investor.getId(), investor.getAmount());

        return findInvestIdByUser(investor.getGoodsId(), investor.getUserId());
    }

    @Transactional
    public InvestGoods removeInvestor(InvestGoods investor) {
        Goods registerGoods = getGoodsMeta(investor.getGoodsId());
        long nowTs = System.currentTimeMillis();
        if (registerGoods == null) {
            throw new ParameterException("goodsId");
        } else if (registerGoods.getRecruitStart().longValue() > nowTs
                ||registerGoods.getRecruitEnd().longValue() < nowTs) {
            throw new RequestException("not recruit invest goods");
        }
        InvestGoods investGoods = findInvestIdByUser(investor.getGoodsId(), investor.getUserId());
        if (investGoods == null) {
            throw new RequestException("I can not cancel.");
        }
        try {
            deleteTradeHistory(investGoods.getId());
            deleteValueHistory(investGoods.getId());
            deletePerformance(investGoods.getId());
            deleteInvestGoods(investGoods.getId());
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException();
        }

        return investGoods;
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
        } else if (StringUtils.isEmpty(target.getCoin()) || StringUtils.isBlank(target.getCoin())) {
            logger.debug("Invalid coin");
            return false;
        } else if (StringUtils.isEmpty(target.getName()) || StringUtils.isBlank(target.getName())) {
            logger.debug("Invalid name");
            return false;
        } else if (StringUtils.isEmpty(target.getDescription()) || StringUtils.isBlank(target.getDescription())) {
            logger.debug("Invalid description");
            return false;
        } else if (target.getAmount() == null) {
            logger.debug("Invalid amount");
            return false;
        } else if (StringUtils.isEmpty(target.getCurrency()) || StringUtils.isBlank(target.getCurrency())) {
            logger.debug("Invalid currency");
            return false;
        } else if (target.getRecruitStart() == null || target.getRecruitEnd() == null
                || String.valueOf(target.getRecruitStart()).length() != 13
                || String.valueOf(target.getRecruitEnd()).length() != 13
                || target.getRecruitStart().longValue() >= target.getRecruitEnd().longValue()) {
//            모집 시작일은 모집 종료일보다 클 수 없음.
            logger.debug("Invalid RecruitDate");
            return false;
        } else if (target.getInvestStart() == null || target.getInvestEnd() == null
                || String.valueOf(target.getInvestStart()).length() != 13
                || String.valueOf(target.getInvestEnd()).length() != 13
                || target.getInvestStart().longValue() >= target.getInvestEnd().longValue()) {
            logger.debug("Invalid investDate");
//            투자 시작일은 모집 종료일보다 클 수 없음.
            return false;
        } else if (target.getBacktestStart() == null || target.getBacktestEnd() == null
                || String.valueOf(target.getBacktestStart()).length() != 13
                || String.valueOf(target.getBacktestEnd()).length() != 13
                || target.getBacktestStart().longValue() >= target.getBacktestEnd().longValue()) {
            logger.debug("Invalid BackTestDate");
//            백테스트 시작일은 모집 종료일보다 클 수 없음.
            return false;
        } else if (target.getRecruitEnd().longValue() >= target.getInvestStart().longValue()) {
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

}