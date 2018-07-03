//package io.gncloud.coin.server.service;
//
//import io.gncloud.coin.server.api.OldStrategyDeployController;
//import AuthenticationException;
//import OperationException;
//import ParameterException;
//import io.gncloud.coin.server.model.OldStrategy;
//import io.gncloud.coin.server.model.OldStrategyDeploy;
//import io.gncloud.coin.server.model.StrategyOrder;
//import org.apache.ibatis.session.SqlSession;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
///*
// * create joonwoo 2018. 6. 4.
// *
// */
//@Service
//public class OldStrategyDeployService {
//
//    private static org.slf4j.Logger logger = LoggerFactory.getLogger(OldStrategyDeployController.class);
//
//    @Autowired
//    private SqlSession sqlSession;
//
//    @Autowired
//    private OldStrategyService oldStrategyService;
//
//    public OldStrategyDeploy insertDeployVersion(OldStrategyDeploy deploy) throws OperationException, ParameterException, AuthenticationException {
//
//        OldStrategy oldStrategy = oldStrategyService.getStrategy(deploy.getId(), deploy.getUserId());
//
//        if (oldStrategy == null) {
//            throw new AuthenticationException();
//        }
//
//        deploy.setCode(oldStrategy.getCode());
////        deploy.setOptions(oldStrategy.getOptions());
//        int cnt = sqlSession.insert("strategyDeploy.insertDeployVersion", deploy);
//        if (cnt != 1) {
//            throw new OperationException("[FAIL] OldStrategy Deploy insert row: " + cnt);
//        }
//        return getDeployLastVersion(deploy.getId());
//    }
//
//    private OldStrategyDeploy getDeployLastVersion (Integer strategyId) {
//        OldStrategyDeploy deploy = new OldStrategyDeploy();
//        deploy.setId(strategyId);
//        return sqlSession.selectOne("strategyDeploy.getLastDeployVersion", deploy);
//    }
//
//    public OldStrategyDeploy getDeployVersion (Integer strategyId, Integer version) {
//        return getDeployVersion (strategyId, version, null);
//    }
//    public OldStrategyDeploy getDeployVersion (Integer strategyId, Integer version, String userId) {
//        OldStrategyDeploy deploy = new OldStrategyDeploy();
//        deploy.setId(strategyId);
//        deploy.setVersion(version);
//        deploy.setUserId(userId);
//        OldStrategyDeploy registerStrategyDeploy = sqlSession.selectOne("strategyDeploy.getDeployVersion", deploy);
//        if (!registerStrategyDeploy.getUserId().equals(userId)) {
//            registerStrategyDeploy.setCode("");
//            registerStrategyDeploy.setBuyer(true);
//        }
//        return registerStrategyDeploy;
//    }
//
//    public List<OldStrategyDeploy> selectDeployVersions (Integer strategyId) {
//        OldStrategyDeploy deploy = new OldStrategyDeploy();
//        deploy.setId(strategyId);
//        return sqlSession.selectList("strategyDeploy.selectDeployVersions", deploy);
//    }
//
//    public OldStrategyDeploy deleteDeployVersion (String userId, Integer strategyId, Integer version) throws AuthenticationException, OperationException, ParameterException {
//        OldStrategyDeploy deploy = getDeployVersion(strategyId, version, userId);
//        if (deploy == null) {
//            throw new ParameterException();
//        }
//        if (!userId.equals(deploy.getUserId())) {
//            throw new AuthenticationException();
//        }
//        if (getStrategyBuyUserCount(strategyId, version) != 0) {
//            return null;
//        }
//        int cnt = sqlSession.delete("strategyDeploy.deleteDeployVersion", deploy);
//        if (cnt != 1) {
//            throw new OperationException();
//        }
//        return deploy;
//    }
//
//    public OldStrategyDeploy saveBackTest(OldStrategyDeploy strategyDeploy) throws AuthenticationException, OperationException {
//        OldStrategyDeploy registerStrategy = getDeployVersion(strategyDeploy.getId(), strategyDeploy.getVersion(), strategyDeploy.getUserId());
//        if (!registerStrategy.getUserId().equals(strategyDeploy.getUserId())) {
//            throw new AuthenticationException();
//        }
//        int updateRow = sqlSession.update("strategyDeploy.saveBackTest", strategyDeploy);
//        if (updateRow != 1) {
//            throw new OperationException("[Sql Exception] update row: " + updateRow);
//        }
//        return registerStrategy;
//    }
//
//    public int getStrategyBuyUserCount (Integer strategyId, Integer version){
//        OldStrategyDeploy strategyDeploy = new OldStrategyDeploy();
//        strategyDeploy.setId(strategyId);
//        strategyDeploy.setVersion(version);
//        return sqlSession.selectOne("strategyDeploy.getStrategyBuyUserCount", strategyDeploy);
//    }
//
//    public Integer getLastSellVersion(Integer strategyId) {
//        OldStrategyDeploy strategyDeploy = new OldStrategyDeploy();
//        strategyDeploy.setId(strategyId);
//        return sqlSession.selectOne("strategyDeploy.getLastSellVersion", strategyDeploy);
//    }
//
//    public void insertSellHistory(StrategyOrder order){
//        sqlSession.insert("strategyDeploy.insertSellHistory", order);
//    }
//
//    public List<OldStrategyDeploy> retrieveOrderStrategy(String userId) {
//        return sqlSession.selectList("strategyDeploy.retrieveOrderStrategy", userId);
//    }
//}