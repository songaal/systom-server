package io.systom.coin.service;

import io.systom.coin.config.TradeConfig;
import io.systom.coin.exception.AuthenticationException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.exception.ParameterException;
import io.systom.coin.exception.RequestException;
import io.systom.coin.model.Goods;
import io.systom.coin.model.InvestGoods;
import io.systom.coin.model.StrategyDeployVersion;
import io.systom.coin.model.TaskResult;
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

        target.setMaxAmount(target.getAmount() / 2);
        target.setMinAmount(target.getAmount() / 100);
        target.setCurrency(target.getCurrency().toLowerCase());
        target.setExchange(target.getExchange().toLowerCase());
        target.setCoin(target.getCoin().toLowerCase());

        try {
//            상품등록
            int changeRow = sqlSession.insert("goods.registerGoods", target);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
//            상품 구매 봇
            InvestGoods paperBot = new InvestGoods();
            paperBot.setGoodsId(target.getId());
            paperBot.setUserId(BOT_USER_ID);
            paperBot.setAmount(BOT_INVEST_AMOUNT);
            paperBot.setExchangeKeyId(BOT_EXCHANGE_KEY_ID);
            changeRow = sqlSession.insert("goods.insertInvestGoods", paperBot);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }

//            상품 구매 봇 퍼포먼스 추가
            changeRow = sqlSession.insert("goods.insertGoodsPerformance", paperBot);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }

        return getGoods(target.getId(), BOT_USER_ID);
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
            return sqlSession.selectList("goods.findInvestIdByUser", investGoods);
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

    public List<Goods> retrieveRecruitGoodsList(long from, long to, String exchange, boolean isDisplay) {
        if (StringUtils.isEmpty(exchange) || StringUtils.isBlank(exchange)) {
            throw new ParameterException("exchange");
        }
        Goods searchGoods = new Goods();
        searchGoods.setRecruitStart(from);
        searchGoods.setRecruitEnd(to);
        searchGoods.setDisplay(isDisplay);
        searchGoods.setExchange(exchange);
        try {
            return sqlSession.selectList("goods.retrieveRecruitGoodsList", searchGoods);
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
    public Goods deleteInvestGoods(Integer id, String userId) {
        Goods registerGoods = getGoodsMeta(id);
        if (registerGoods == null) {
            throw new ParameterException("GoodsId");
        } else if (!registerGoods.getUserId().equals(userId)) {
            throw new AuthenticationException();
        }

        List<InvestGoods> registerInvestGoodsList = findInvestIdByUserList(id);
        try {
            int changeRow = 0;
            for(int i=0; i < registerInvestGoodsList.size(); i++) {
                InvestGoods investGoods = registerInvestGoodsList.get(i);

                sqlSession.delete("goods.deleteTradeHistory", investGoods.getId());
                logger.debug("DELETE {}/{} STEP 1/4 >> userId: {} TradeHistory rows: {}", i, registerInvestGoodsList.size(), investGoods.getUserId(), changeRow);

                changeRow = sqlSession.delete("goods.deletePerformance", investGoods.getId());
                logger.debug("DELETE {}/{} STEP 2/4 >> userId: {} Performance rows: {}", i, registerInvestGoodsList.size(), investGoods.getUserId(), changeRow);

                changeRow = sqlSession.delete("goods.deleteValueHistory", investGoods.getId());
                logger.debug("DELETE {}/{} STEP 3/4 >> userId: {} valueHistory rows: {}", i, registerInvestGoodsList.size(), investGoods.getUserId(), changeRow);

                changeRow = sqlSession.delete("goods.deleteInvestGoods", investGoods.getId());
                logger.debug("DELETE {}/{} STEP 4/4 >> userId: {} InvestGoods rows: {}", i, registerInvestGoodsList.size(), investGoods.getUserId(), changeRow);
            }
            changeRow = sqlSession.delete("goods.deleteGoods", id);
            logger.debug("LAST DELETE GOODS row: {}", changeRow);

        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }

        return registerGoods;
    }

    public Goods updateRecruitGoods(Goods target) {
        Goods registerGoods = getGoodsMeta(target.getId());
        if (registerGoods == null) {
            throw new ParameterException("inValid GoodsId");
        } else if (!registerGoods.getUserId().equals(target.getUserId())) {
            throw new AuthenticationException();
        } else if (registerGoods.getInvestStart() >= System.currentTimeMillis()) {
            throw new RequestException("inValid Goods");
        } else if (!isGoodsValidation(target)) {
            throw new ParameterException("Require");
        }


//        TODO 수정 로직..


        return null;
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
                || target.getRecruitStart() >= target.getRecruitEnd()) {
//            모집 시작일은 모집 종료일보다 클 수 없음.
            logger.debug("Invalid RecruitDate");
            return false;
        } else if (target.getInvestStart() == null || target.getInvestEnd() == null
                || String.valueOf(target.getInvestStart()).length() != 13
                || String.valueOf(target.getInvestEnd()).length() != 13
                || target.getInvestStart() >= target.getInvestEnd()) {
            logger.debug("Invalid investDate");
//            투자 시작일은 모집 종료일보다 클 수 없음.
            return false;
        } else if (target.getBacktestStart() == null || target.getBacktestEnd() == null
                || String.valueOf(target.getBacktestStart()).length() != 13
                || String.valueOf(target.getBacktestEnd()).length() != 13
                || target.getBacktestStart() >= target.getBacktestEnd()) {
            logger.debug("Invalid BackTestDate");
//            백테스트 시작일은 모집 종료일보다 클 수 없음.
            return false;
        } else if (target.getRecruitEnd() >= target.getInvestStart()) {
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