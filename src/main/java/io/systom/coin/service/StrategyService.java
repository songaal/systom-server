package io.systom.coin.service;

import io.systom.coin.exception.AuthenticationException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.exception.ParameterException;
import io.systom.coin.model.Strategy;
import io.systom.coin.utils.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * create joonwoo 2018. 3. 22.
 * 
 */
@Service
public class StrategyService {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(StrategyService.class);

    @Autowired private SqlSession sqlSession;
    @Autowired private StrategyDeployService strategyDeployService;

    public List<Strategy> retrieveStrategiesByUserId(String userId) throws OperationException {
        try {
            return sqlSession.selectList("strategy.retrieveStrategiesByUserId", userId);
        }catch (Exception e){
            logger.error("", e);
            throw new OperationException();
        }
    }

    public Strategy createBlankStrategy(Strategy strategy) throws OperationException, ParameterException {
        if (StringUtils.isBlank(strategy.getName()) || StringUtils.isEmpty(strategy.getName())){
            throw new ParameterException("strategy name");
        }

//        Strategy uniqueStrategy = findStrategyByName(strategy.getName(), strategy.getAuthorId());
//        if (uniqueStrategy != null) {
//            throw new ParameterException("strategy unique name");
//        }

        try {
            int changeRow = sqlSession.insert("strategy.createBlankStrategy", strategy);
            if(changeRow != 1){
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
            return findStrategyById(strategy.getId(), strategy.getUserId());
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

    public Strategy findStrategyById(Integer id, String userId) throws ParameterException, OperationException {
        if (id == null){
            throw new ParameterException("strategy id");
        }
        try {
            Strategy registerStrategy = sqlSession.selectOne("strategy.findStrategyById", new Strategy(id, userId));
            if (registerStrategy != null && !registerStrategy.getUserId().equals(userId)){
                registerStrategy.setCode(null);
            }
            return registerStrategy;
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

    public Strategy findStrategyByName(String name, String userId) throws ParameterException, OperationException {
        if (name == null){
            throw new ParameterException("strategy name");
        }
        try {
            Strategy registerStrategy = sqlSession.selectOne("strategy.findStrategyByName", name);
            if (registerStrategy != null && !registerStrategy.getUserId().equals(userId)){
                registerStrategy.setCode(null);
            }
            return registerStrategy;
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

    public Strategy updateStrategy(Strategy target) throws ParameterException, OperationException, AuthenticationException {
        if (target.getId() == null || target.getUserId() == null){
            throw new ParameterException("required");
        }

        Strategy source = findStrategyById(target.getId(), target.getUserId());
        if(!source.getUserId().equals(target.getUserId())){
            throw new AuthenticationException();
        }

        target.setName(source.getName());

        try {
            int changeRow = sqlSession.update("strategy.updateStrategy", target);
            if(changeRow != 1){
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
            return findStrategyById(target.getId(), target.getUserId());
        } catch (Exception e){
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

    public Strategy deleteStrategy(Strategy target) throws ParameterException, OperationException, AuthenticationException {
        if (target.getId() == null || StringUtils.isEmpty(target.getUserId()) || StringUtils.isBlank(target.getUserId())){
            throw new ParameterException("required");
        }

        Strategy source = findStrategyById(target.getId(), target.getUserId());
        if(!source.getUserId().equals(target.getUserId())){
            throw new AuthenticationException();
        }

        strategyDeployService.deleteAllDeployVersion(source);

        try {
            int changeRow = sqlSession.delete("strategy.deleteStrategy", source);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch(Exception e){
            throw new OperationException("[FAIL] deleteStrategy");
        }
        return source;
    }


}