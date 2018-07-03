//package io.gncloud.coin.server.service;
//
//import AbstractException;
//import AuthenticationException;
//import OperationException;
//import ParameterException;
//import io.gncloud.coin.server.model.OldStrategy;
//import org.apache.ibatis.session.SqlSession;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
///*
// * create joonwoo 2018. 3. 22.
// *
// */
//@Service
//public class OldStrategyService {
//
//    private static org.slf4j.Logger logger = LoggerFactory.getLogger(OldStrategyService.class);
//
//    @Autowired
//    private SqlSession sqlSession;
//
//    @Autowired
//    private TaskService taskService;
//
//    public OldStrategy insertStrategy(OldStrategy oldStrategy) throws OperationException, ParameterException, AuthenticationException {
//
//        isNotNull(oldStrategy.getUserId(), "userId");
//        isNotNull(oldStrategy.getCode(), "code");
////        isNotNull(oldStrategy.getOptions(), "options");
//
//        logger.debug("INSERT OldStrategy: {}", oldStrategy);
//        try {
//            int result = sqlSession.insert("oldStrategy.insertStrategy", oldStrategy);
//            if(result != 1){
//                throw new OperationException("[FAIL] Insert Failed OldStrategy. result count: " + result);
//            }
//            return getStrategy(oldStrategy.getId(), oldStrategy.getUserId());
//        } catch (Exception e){
//            logger.error("", e);
//            throw new OperationException("[FAIL] Insert Failed OldStrategy");
//        }
//    }
//
//    public OldStrategy getStrategy(Integer strategyId, String userId) throws ParameterException, OperationException, AuthenticationException {
//        isNotNull(strategyId, "strategyId");
//        isNotNull(userId, "userId");
//        OldStrategy oldStrategy = new OldStrategy(strategyId, userId);
//        try {
//            oldStrategy = sqlSession.selectOne("oldStrategy.getStrategy", oldStrategy);
//            if (!oldStrategy.getUserId().equals(userId)) {
//                oldStrategy.setCode("");
//                oldStrategy.setBuyer(true);
//            }
//        } catch (Exception e){
//            throw new OperationException("[FAIL] Update Failed OldStrategy: " + strategyId);
//        }
//        return oldStrategy;
//    }
//
//    public List<OldStrategy> retrieveStrategiesByUserId(String userId) throws ParameterException, OperationException {
//        OldStrategy oldStrategy = new OldStrategy();
//        oldStrategy.setUserId(userId);
//        try {
//            return sqlSession.selectList("oldStrategy.selectStrategy", oldStrategy);
//        }catch (Exception e){
//            logger.error("", e);
//            throw new OperationException("[FAIL] Failed OldStrategy");
//        }
//    }
//
//    public OldStrategy updateStrategy(OldStrategy newOldStrategy, String userId) throws ParameterException, OperationException, AuthenticationException {
//
//        isNotNull(userId, "userId");
//        isNotNull(newOldStrategy.getId(), "strategyId");
//        isNotNull(newOldStrategy.getCode(), "code");
////        isNotNull(newOldStrategy.getOptions(), "options");
//
//        OldStrategy oldStrategy = getStrategy(newOldStrategy.getId(), userId);
//
//        if(!oldStrategy.getUserId().equals(userId)){
//            throw new AuthenticationException("You do not have permission.");
//        }
//
//        oldStrategy.setCode(newOldStrategy.getCode());
////        oldStrategy.setOptions(newOldStrategy.getOptions());
//
//        logger.debug("UPDATE OldStrategy: {}", oldStrategy);
//
//        try {
//            int result = sqlSession.update("oldStrategy.updateStrategy", oldStrategy);
//            if(result != 1){
//                throw new OperationException("[FAIL] Update Failed OldStrategy result count: " + result);
//            }
//            return oldStrategy;
//        } catch (Exception e){
//            throw new OperationException("[FAIL] Update Failed OldStrategy");
//        }
//    }
//
//    public OldStrategy deleteStrategy(Integer strategyId, String userId) throws ParameterException, OperationException, AuthenticationException {
//        isNotNull(strategyId, "strategyId");
//
//        OldStrategy oldStrategy = getStrategy(strategyId, userId);
//        if (oldStrategy == null) {
//            throw new AuthenticationException("You do not have permission.");
//        }
//        try {
//            taskService.deleteBackTestHistory(oldStrategy);
//            int result = sqlSession.delete("oldStrategy.deleteStrategy", oldStrategy);
//            if (result != 1) {
//                throw new OperationException("[FAIL] deleteStrategy resultCount: " + result);
//            }
//        } catch(AbstractException e){
//            throw new OperationException("[FAIL] deleteStrategy");
//        }
//        return oldStrategy;
//    }
//
//    private void isNotNull(String field, String label) throws ParameterException {
//        if(field == null || "".equals(field)){
//            throw new ParameterException(label);
//        }
//    }
//    private void isNotNull(Integer field, String label) throws ParameterException {
//        if(field == null){
//            throw new ParameterException(label);
//        }
//    }
//    private void isNotNull(OldStrategy oldStrategy, String label) throws ParameterException {
//        if(oldStrategy == null){
//            throw new ParameterException(label);
//        }
//    }
//}