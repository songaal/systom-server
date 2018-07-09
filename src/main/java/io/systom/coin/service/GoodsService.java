package io.systom.coin.service;

import io.systom.coin.config.TradeConfig;
import io.systom.coin.exception.AuthenticationException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.exception.ParameterException;
import io.systom.coin.exception.RequestException;
import io.systom.coin.model.Goods;
import io.systom.coin.model.InvestGoods;
import io.systom.coin.model.StrategyDeployVersion;
import io.systom.coin.utils.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

    public static final String DATE_FORMAT = "yyyyMMdd";
    @Transactional
    public Goods registerInvestGoods(Goods target){

        if (!identityService.isManager(target.getUserId())){
            throw new AuthenticationException();
        } else if (!isGoodsValidation(target)) {
            throw new ParameterException("Require");
        }

        target.setCashUnit(target.getCashUnit().toLowerCase());
        target.setExchange(target.getExchange().toLowerCase());
        target.setCoinUnit(target.getCoinUnit().toLowerCase());

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
        try {
            return  sqlSession.selectOne("goods.getGoods", searchGoods);
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

    public Goods updateGoodsHide(Integer id, String userId) {
        Goods registerGoods = getGoods(id);
        int nowTime = Integer.parseInt(new SimpleDateFormat(DATE_FORMAT).format(new Date()));
        if (registerGoods == null || registerGoods.getInvestStart().intValue() <= nowTime) {
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
        int nowTime = Integer.parseInt(new SimpleDateFormat(DATE_FORMAT).format(new Date()));
        if (registerGoods == null || registerGoods.getInvestStart().intValue() <= nowTime) {
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
        if (registerInvestGoodsList == null || registerInvestGoodsList.size() > 0) {
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
        } else if (registerGoods.getRecruitEnd().longValue() < nowTime) {
            throw new RequestException("Goods that cannot be modified");
        } else if (!isGoodsValidation(target)) {
            throw new ParameterException("Require");
        }

        if (!registerGoods.getTestStart().equals(target.getTestStart())
                ||!registerGoods.getTestEnd().equals(target.getTestEnd())) {
//            백테스트 시간이 변경되면 기존 백테스트 전체 삭제
            target.setTestMonthlyReturn(null);
            target.setTestReturnPct(0f);
        }

        target.setExchange(target.getExchange().toLowerCase());
        target.setCashUnit(target.getCashUnit().toLowerCase());
        target.setBaseUnit(target.getCashUnit().toLowerCase());
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
                || target.getRecruitStart().intValue() > target.getRecruitEnd().intValue()) {
//            모집 시작일은 모집 종료일보다 클 수 없음.
            logger.debug("Invalid RecruitDate");
            return false;
        } else if (target.getInvestStart() == null || target.getInvestEnd() == null
                || String.valueOf(target.getInvestStart()).length() != 8
                || String.valueOf(target.getInvestEnd()).length() != 8
                || target.getInvestStart().intValue() > target.getInvestEnd().intValue()) {
            logger.debug("Invalid investDate");
//            투자 시작일은 모집 종료일보다 클 수 없음.
            return false;
        } else if (target.getTestStart() == null || target.getTestEnd() == null
                || String.valueOf(target.getTestStart()).length() != 8
                || String.valueOf(target.getTestEnd()).length() != 8
                || target.getTestStart().intValue() > target.getTestEnd().intValue()) {
            logger.debug("Invalid BackTestDate");
//            백테스트 시작일은 모집 종료일보다 클 수 없음.
            return false;
        } else if (target.getRecruitEnd().longValue() > target.getInvestStart().longValue()) {
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