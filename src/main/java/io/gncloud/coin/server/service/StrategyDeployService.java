package io.gncloud.coin.server.service;

import io.gncloud.coin.server.api.StrategyDeployController;
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
 * create joonwoo 2018. 6. 4.
 * 
 */
@Service
public class StrategyDeployService {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(StrategyDeployController.class);

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private StrategyService strategyService;

    public StrategyDeploy insertDeployVersion(StrategyDeploy deploy) throws OperationException, ParameterException, AuthenticationException {

        Strategy strategy = strategyService.getStrategy(deploy.getId(), deploy.getUserId());

        if (strategy == null) {
            throw new AuthenticationException();
        }

        deploy.setCode(strategy.getCode());
        deploy.setOptions(strategy.getOptions());
        int cnt = sqlSession.insert("strategyDeploy.insertDeployVersion", deploy);
        if (cnt != 1) {
            throw new OperationException("[FAIL] Strategy Deploy insert row: " + cnt);
        }
        return getDeployLastVersion(deploy.getId());
    }

    private StrategyDeploy getDeployLastVersion (Integer strategyId) {
        StrategyDeploy deploy = new StrategyDeploy();
        deploy.setId(strategyId);
        return sqlSession.selectOne("strategyDeploy.getLastDeployVersion", deploy);
    }

    public StrategyDeploy getDeployVersion (Integer strategyId, Integer version, String userId) {
        StrategyDeploy deploy = new StrategyDeploy();
        deploy.setId(strategyId);
        deploy.setVersion(version);
        StrategyDeploy registerStrategyDeploy = sqlSession.selectOne("strategyDeploy.getDeployVersion", deploy);
        if (!registerStrategyDeploy.getUserId().equals(userId)) {
            registerStrategyDeploy.setCode("");
            registerStrategyDeploy.setBuyer(true);
        }
        return registerStrategyDeploy;
    }

    public List<StrategyDeploy> selectDeployVersions (Integer strategyId) {
        StrategyDeploy deploy = new StrategyDeploy();
        deploy.setId(strategyId);
        return sqlSession.selectList("strategyDeploy.selectDeployVersions", deploy);
    }

    public StrategyDeploy deleteDeployVersion (String userId, Integer strategyId, Integer version) throws AuthenticationException, OperationException, ParameterException {
        StrategyDeploy deploy = getDeployVersion(strategyId, version, userId);
        if (deploy == null) {
            throw new ParameterException();
        }
        if (!userId.equals(deploy.getUserId())) {
            throw new AuthenticationException();
        }
        int cnt = sqlSession.delete("strategyDeploy.deleteDeployVersion", deploy);
        if (cnt != 1) {
            throw new OperationException();
        }
        return deploy;
    }

    public StrategyDeploy saveBackTest(StrategyDeploy strategyDeploy) throws AuthenticationException, OperationException {
        StrategyDeploy registerStrategy = getDeployVersion(strategyDeploy.getId(), strategyDeploy.getVersion(), strategyDeploy.getUserId());
        if (!registerStrategy.getUserId().equals(strategyDeploy.getUserId())) {
            throw new AuthenticationException();
        }
        int updateRow = sqlSession.update("strategyDeploy.saveBackTest", strategyDeploy);
        if (updateRow != 1) {
            throw new OperationException("[Sql Exception] update row: " + updateRow);
        }
        return registerStrategy;
    }

}