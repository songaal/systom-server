package io.systom.coin.service;//package io.gncloud.coin.server.service;

import io.systom.coin.exception.AuthenticationException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.exception.ParameterException;
import io.systom.coin.model.Goods;
import io.systom.coin.model.StrategyDeployVersion;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * create joonwoo 2018. 6. 8.
 *
 */
@Service
public class MarketplaceService2 {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(MarketplaceService2.class);

    @Autowired private SqlSession sqlSession;
    @Autowired private StrategyDeployService strategyDeployService;


    public Goods releases(String userId, Goods goods) throws ParameterException, OperationException, AuthenticationException {
        StrategyDeployVersion target = new StrategyDeployVersion();
        target.setUserId(userId);
        target.setId(goods.getStrategyId());
        target.setVersion(goods.getVersion());

        StrategyDeployVersion registerDeployVersion = strategyDeployService.getDeployVersion(target);

        if (registerDeployVersion == null) {
            throw new AuthenticationException();
        }
        return goods;
    }



    public Goods getGoods(Integer id) {
        Goods registerGoods = null;
        try {
            registerGoods = sqlSession.selectOne("", id);
        } catch (Exception e) {
            logger.error("", e);
        }
        return registerGoods;
    }





}