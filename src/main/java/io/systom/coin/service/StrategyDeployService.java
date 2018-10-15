package io.systom.coin.service;

import io.systom.coin.exception.AuthenticationException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.exception.ParameterException;
import io.systom.coin.model.Strategy;
import io.systom.coin.model.StrategyDeployVersion;
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

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(StrategyDeployService.class);

    @Autowired
    private SqlSession sqlSession;
    @Autowired
    private StrategyService strategyService;

    public List<StrategyDeployVersion> createDeployVersion(StrategyDeployVersion target) throws AuthenticationException, ParameterException, OperationException {
        if (target.getId() == null || target.getUserId() == null) {
            throw new ParameterException("required");
        }

        Strategy strategy = strategyService.findStrategyById(target.getId(), target.getUserId());
        if (!strategy.getUserId().equals(target.getUserId())){
            throw new AuthenticationException();
        }

        target.setCode(strategy.getCode());

        try {
            int changeRow = sqlSession.insert("strategyDeploy.createDeployVersion", target);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
        return retrieveDeployVersions(strategy);
    }

    public List<StrategyDeployVersion> retrieveDeployVersions(Strategy target) throws ParameterException, OperationException {
        if (target.getId() == null){
            throw new ParameterException("required");
        }
        try {
            return sqlSession.selectList("strategyDeploy.retrieveDeployVersions", target);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

    public StrategyDeployVersion getDeployVersion (StrategyDeployVersion target) throws ParameterException, OperationException {
        if (target.getId() == null || target.getVersion() == null) {
            throw new ParameterException("required");
        }
        try {
            StrategyDeployVersion registerDeployVersion = sqlSession.selectOne("strategyDeploy.getDeployVersion", target);
            if (target.getUserId() == null || !registerDeployVersion.getUserId().equals(target.getUserId())) {
                registerDeployVersion.setCode(null);
            }
            return registerDeployVersion;
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

    public StrategyDeployVersion deleteDeployVersion(StrategyDeployVersion target) throws ParameterException, AuthenticationException, OperationException {
        if (target.getId() == null || target.getUserId() == null || target.getVersion() == null) {
            throw new ParameterException("required");
        }
        StrategyDeployVersion registerVersion = getDeployVersion(target);

        if (!registerVersion.getUserId().equals(target.getUserId())) {
            throw new AuthenticationException();
        }
        registerVersion.setTrash(true);
        try {
            int changeRow = sqlSession.update("strategyDeploy.deleteDeployVersion", registerVersion);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }

        return getDeployVersion(registerVersion);
    }

    public List<StrategyDeployVersion> deleteAllDeployVersion(Strategy target) throws ParameterException, AuthenticationException, OperationException {
        if (target.getId() == null || target.getUserId() == null) {
            throw new ParameterException("required");
        }
        Strategy registerStrategy = strategyService.findStrategyById(target.getId(), target.getUserId());

        if (!registerStrategy.getUserId().equals(target.getUserId())) {
            throw new AuthenticationException();
        }

        try {
            sqlSession.update("strategyDeploy.deleteAllDeployVersion", registerStrategy);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }

        return retrieveDeployVersions(registerStrategy);
    }

    public String getStrategyModel (Integer strategyId, Integer version) throws ParameterException, OperationException {
        StrategyDeployVersion deployVersion = new StrategyDeployVersion();
        deployVersion.setId(strategyId);
        deployVersion.setVersion(version);
        try {
            String model = sqlSession.selectOne("strategyDeploy.getStrategyModel", deployVersion);
            return model;
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }
}