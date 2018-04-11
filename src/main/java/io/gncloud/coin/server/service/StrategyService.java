package io.gncloud.coin.server.service;

import io.gncloud.coin.server.exception.AbstractException;
import io.gncloud.coin.server.exception.AuthenticationException;
import io.gncloud.coin.server.exception.OperationException;
import io.gncloud.coin.server.exception.ParameterException;
import io.gncloud.coin.server.model.Strategy;
import io.gncloud.coin.server.message.RunBackTestRequest;
import io.gncloud.coin.server.model.User;
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

    @Autowired
    private SqlSession sqlSession;

    public Strategy insertStrategy(Strategy strategy) throws OperationException, ParameterException {

        isNotNull(strategy.getUserId(), "userId");
        isNotNull(strategy.getCode(), "code");
        isNotNull(strategy.getOptions(), "options");

        logger.debug("INSERT Strategy: {}", strategy);
        try {
            int result = sqlSession.insert("strategy.insertStrategy", strategy);
            if(result != 1){
                throw new OperationException("[FAIL] Insert Failed Strategy. result count: " + result);
            }
            return getStrategy(strategy.getId());
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] Insert Failed Strategy");
        }
    }

    public Strategy getStrategy(String strategyId) throws ParameterException, OperationException {
        return getStrategy(strategyId, null);
    }

    public Strategy getStrategy(String strategyId, String userId) throws ParameterException, OperationException {
        isNotNull(strategyId, "strategyId");

        Strategy strategy = new Strategy(strategyId, userId);
        try {
            strategy = sqlSession.selectOne("strategy.getStrategy", strategy);
        } catch (Exception e){
            throw new OperationException("[FAIL] Update Failed Strategy: " + strategyId);
        }
        return strategy;
    }


    public List<Strategy> findStrategyByUser(String userId) throws ParameterException, OperationException {
        Strategy strategy = new Strategy();
        strategy.setUserId(userId);
        try {
            return sqlSession.selectList("strategy.getStrategy", strategy);
        }catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] Update Failed Strategy");
        }
    }

    public Strategy updateStrategy(Strategy newStrategy, String userId) throws ParameterException, OperationException, AuthenticationException {

        isNotNull(userId, "userId");
        isNotNull(newStrategy.getId(), "strategyId");
        isNotNull(newStrategy.getCode(), "code");
        isNotNull(newStrategy.getOptions(), "options");

        Strategy strategy = getStrategy(userId, newStrategy.getId());

        if(!strategy.getUserId().equals(userId)){
            throw new AuthenticationException("You do not have permission.");
        }

        strategy.setCode(newStrategy.getCode());
        strategy.setOptions(newStrategy.getOptions());
        strategy.setName(newStrategy.getName());

        logger.debug("UPDATE Strategy: {}", strategy);

        try {
            int result = sqlSession.update("strategy.updateStrategy", strategy);
            if(result != 1){
                throw new OperationException("[FAIL] Update Failed Strategy result count: " + result);
            }
            return strategy;
        } catch (Exception e){
            throw new OperationException("[FAIL] Update Failed Strategy");
        }
    }

    public Strategy deleteStrategy(String strategyId, String userId) throws ParameterException, OperationException {
        isNotNull(strategyId, "strategyId");

        Strategy strategy = getStrategy(strategyId, userId);

        try {
            if(strategy != null) {
                int result = sqlSession.delete("strategy.deleteStrategy", strategyId);
                if (result != 1) {
                    throw new OperationException("[FAIL] deleteStrategy resultCount: " + result);
                }
            }
        } catch(AbstractException e){
            throw new OperationException("[FAIL] deleteStrategy");
        }
        return strategy;
    }

    private void isNotNull(String field, String label) throws ParameterException {
        if(field == null || "".equals(field)){
            throw new ParameterException(label);
        }
    }
    private void isNotNull(Strategy strategy, String label) throws ParameterException {
        if(strategy == null){
            throw new ParameterException(label);
        }
    }

    public RunBackTestRequest.ExchangeAuth getExchangeAuth(String token, String exchangeName, String userPin) {

        //Todo db에서 auth를 조회한뒤 userPin번호로 디코딩한다.

        return new RunBackTestRequest.ExchangeAuth();
    }
}