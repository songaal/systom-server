package io.systom.coin.service;

import io.systom.coin.config.TradeConfig;
import io.systom.coin.exception.OperationException;
import io.systom.coin.exception.ParameterException;
import io.systom.coin.exception.RequestException;
import io.systom.coin.model.ExchangeKey;
import io.systom.coin.model.Goods;
import io.systom.coin.model.InvestGoods;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
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
    private TradeConfig tradeConfig;
    @Autowired
    private ExchangeService exchangeService;
    @Autowired
    private GoodsService goodsService;

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

    public InvestGoods registrationInvestor(InvestGoods investor) {
        Goods registerGoods = goodsService.getGoods(investor.getGoodsId());
        long nowTs = System.currentTimeMillis();
        if (registerGoods == null || !registerGoods.getDisplay()) {

            throw new RequestException("invalid goods");
        } else if (registerGoods.getRecruitStart().intValue() > nowTs
                ||registerGoods.getRecruitEnd().intValue() < nowTs) {

            throw new RequestException("not recruit invest goods");
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

        return investor;
    }

    public InvestGoods removeInvestor(InvestGoods investor) {
        Goods registerGoods = goodsService.getGoods(investor.getGoodsId());
        int nowTime = Integer.parseInt(new SimpleDateFormat("yyyymmdd").format(new Date()));

        if (registerGoods == null) {
            throw new ParameterException("goodsId");
        } else if (registerGoods.getRecruitStart().intValue() > nowTime
                ||registerGoods.getRecruitEnd().intValue() < nowTime) {
            throw new RequestException("not recruit invest goods");
        }

        InvestGoods investGoods = findInvestGoodsByUser(investor.getGoodsId(), investor.getUserId());
        if (investGoods == null) {
            throw new RequestException("I can not cancel.");
        }

        try {
            int changeRow = sqlSession.delete("investGoods.deleteInvestGoods", investGoods.getId());
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException();
        }

        return investGoods;
    }



}