package io.gncloud.coin.server.service;

import io.gncloud.coin.server.exception.AuthenticationException;
import io.gncloud.coin.server.exception.OperationException;
import io.gncloud.coin.server.exception.ParameterException;
import io.gncloud.coin.server.model.Strategy;
import io.gncloud.coin.server.model.StrategyDeploy;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * create joonwoo 2018. 6. 8.
 * 
 */
@Service
public class MarketplaceService {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(MarketplaceService.class);

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private StrategyDeployService strategyDeployService;

    @Autowired
    private PaymentService paymentService;

    public List<StrategyDeploy> retrieveStrategyMarketList() throws OperationException {
        try {
            List<StrategyDeploy> registerStrategies = sqlSession.selectList("marketplace.retrieveStrategyMarketList");
            return registerStrategies;
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException(e.getMessage());
        }
    }

    public StrategyDeploy registerStrategyMarket(StrategyDeploy strategyDeploy) throws ParameterException, OperationException, AuthenticationException {
        isNotNull(strategyDeploy, "strategy");
        isNotNull(strategyDeploy.getId(), "strategyId");
        isNotNull(strategyDeploy.getUserId(), "userId");
        isNotNull(strategyDeploy.getVersion(), "version");
        isNotNull(strategyDeploy.getPrice(), "price");
        try {
            StrategyDeploy registerStrategy = strategyDeployService.getDeployVersion(strategyDeploy.getId(), strategyDeploy.getVersion(), strategyDeploy.getUserId());

            if (!registerStrategy.getUserId().equals(strategyDeploy.getUserId())) {
                throw new AuthenticationException();
            }
            if (registerStrategy.getIsSell().equals("selling")) {
                logger.warn("Already sold.", registerStrategy);
                throw new OperationException("SQL Exception. update row count: ");
            }

            int updateRowCount = sqlSession.update("marketplace.registerStrategyMarket", strategyDeploy);
            if (updateRowCount != 1) {
                throw new OperationException("SQL Exception. update row count: " + updateRowCount);
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new OperationException(e.getMessage());
        }
        return strategyDeployService.getDeployVersion(strategyDeploy.getId(), strategyDeploy.getVersion(), strategyDeploy.getUserId());
    }


    private void isNotNull(String field, String label) throws ParameterException {
        if(field == null || "".equals(field)){
            throw new ParameterException(label);
        }
    }
    private void isNotNull(Integer field, String label) throws ParameterException {
        if(field == null){
            throw new ParameterException(label);
        }
    }
    private void isNotNull(Strategy strategy, String label) throws ParameterException {
        if(strategy == null){
            throw new ParameterException(label);
        }
    }
    private void isNotNull(Float field, String label) throws ParameterException {
        if(field == null){
            throw new ParameterException(label);
        }
    }


}