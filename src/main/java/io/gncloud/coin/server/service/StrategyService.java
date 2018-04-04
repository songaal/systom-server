package io.gncloud.coin.server.service;

import io.gncloud.coin.server.exception.AuthenticationException;
import io.gncloud.coin.server.exception.OperationException;
import io.gncloud.coin.server.exception.ParameterException;
import io.gncloud.coin.server.model.Strategy;
import io.gncloud.coin.server.model.User;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/*
 * create joonwoo 2018. 3. 22.
 * 
 */
@Service
public class StrategyService {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(StrategyService.class);

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private IdentityService identityService;

    public Strategy insertStrategy(String token, Strategy createStrategy) throws OperationException, ParameterException, AuthenticationException {

        createStrategy.setUserId(identityService.findTokenByUser(token).getUserId());

        isNull(createStrategy.getUserId(), "userId");
        isNull(createStrategy.getCode(), "code");
        isNull(createStrategy.getOptions(), "options");

        createStrategy.setId(UUID.randomUUID().toString());

        logger.debug("INSERT Strategy: {}", createStrategy);
        try {
            int result = sqlSession.insert("strategy.insertStrategy", createStrategy);
            if(result != 1){
                throw new OperationException("[FAIL] Insert Failed Strategy. result count: " + result);
            }
            return getStrategy(token, createStrategy.getId());
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] Insert Failed Strategy");
        } catch (AuthenticationException e) {
            throw e;
        }
    }

    public Strategy getStrategy(String token, String strategyId) throws ParameterException, AuthenticationException, OperationException {
        isNull(strategyId, "strategyId");

        Strategy findStrategy = new Strategy();
        findStrategy.setId(strategyId);
        return getStrategy(token, findStrategy);
    }
    public Strategy getStrategy(String token, Strategy strategyId) throws ParameterException, AuthenticationException, OperationException {
        isNull(strategyId, "strategy");

        Strategy registerStrategy = null;
        try {
            registerStrategy = sqlSession.selectOne("strategy.getStrategy", strategyId);
            if(registerStrategy == null){
                throw new OperationException("[FAIL] Update Failed Strategy result");
            }
        } catch (Exception e){
            throw new OperationException("[FAIL] Update Failed Strategy");
        }

        User user = identityService.findTokenByUser(token);
        if(!registerStrategy.getUserId().equals(user.getUserId())){
            throw new AuthenticationException("You do not have permission.");
        }
        return registerStrategy;
    }

    public List<Strategy> findTokenByStrategy(String token) throws ParameterException, OperationException {
        String userId = identityService.findTokenByUser(token).getUserId();

        Strategy findStrategy = new Strategy();
        findStrategy.setUserId(userId);
        List<Strategy> strategyList = null;
        try {
            strategyList = sqlSession.selectList("strategy.getStrategy", findStrategy);
        }catch (Exception e){
            throw new OperationException("[FAIL] Update Failed Strategy");
        }

        return strategyList;
    }

    public Strategy updateStrategy(String token, Strategy strategy) throws ParameterException, OperationException, AuthenticationException {

        isNull(strategy.getId(), "algoId");
        isNull(strategy.getCode(), "code");
        isNull(strategy.getOptions(), "options");

        User user = identityService.findTokenByUser(token);
        Strategy registerStrategy = getStrategy(token, strategy.getId());

        if(!registerStrategy.getUserId().equals(user.getUserId())){
            throw new AuthenticationException("You do not have permission.");
        }

        registerStrategy.setCode(strategy.getCode());
        registerStrategy.setOptions(strategy.getOptions());
        registerStrategy.setName(strategy.getName());

        logger.debug("UPDATE Strategy: {}", registerStrategy);

        try {
            int result = sqlSession.update("strategy.updateStrategy", registerStrategy);
            if(result != 1){
                throw new OperationException("[FAIL] Update Failed Strategy result count: " + result);
            }
            return getStrategy(token, registerStrategy);
        } catch (Exception e){
            throw new OperationException("[FAIL] Update Failed Strategy");
        }
    }

    public void deleteAlgo(String strategyId) throws ParameterException, OperationException {
        isNull(strategyId, "strateId");

        logger.debug("DELETE ALGO: {}", strategyId);
        int result = sqlSession.delete("strategy.deleteStrategy", strategyId);
        if(result != 1){
            throw new OperationException("[FAIL] delete Filed Strategy");
        }
    }

    private void isNull(String field, String label) throws ParameterException {
        if(field == null || "".equals(field)){
            throw new ParameterException(label);
        }
    }
    private void isNull(Strategy strategy, String label) throws ParameterException {
        if(strategy == null){
            throw new ParameterException(label);
        }
    }
}