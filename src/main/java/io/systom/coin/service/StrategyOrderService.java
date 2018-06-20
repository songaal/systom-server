//package io.gncloud.coin.server.service;
//
//import OperationException;
//import ParameterException;
//import RequestException;
//import io.gncloud.coin.server.model.*;
//import org.apache.ibatis.session.SqlSession;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
///*
// * create joonwoo 2018. 6. 11.
// *
// */
//@Service
//public class StrategyOrderService {
//    private static org.slf4j.Logger logger = LoggerFactory.getLogger(StrategyOrderService.class);
//
//    @Autowired
//    private SqlSession sqlSession;
////    TODO [스케쥴] 결재 만료 여부, 만료시 상태 stop 처리
//
//    @Autowired
//    private UserCoinService userCoinService;
//
//    @Autowired
//    private OldStrategyDeployService oldStrategyDeployService;
//
//    @Autowired
//    private StrategyStatusService StrategyStatusService;
//
//
//    @Transactional
//    public StrategyStatus order(StrategyOrder strategyOrder) throws ParameterException, RequestException, OperationException, Exception {
//        isNotNull(strategyOrder.getId(), "strategyId");
//        isNotNull(strategyOrder.getVersion(), "version");
//
//        StrategyStatus status = StrategyStatusService.getStatus(strategyOrder.getUserId(), strategyOrder.getId(), strategyOrder.getVersion());
//        if (status != null) {
//            new OperationException("Purchased OldStrategy");
//        }
//
//        OldStrategyDeploy registerDeployVersion = oldStrategyDeployService.getDeployVersion(strategyOrder.getId(), strategyOrder.getVersion());
//        if (registerDeployVersion == null || !"selling".equalsIgnoreCase(registerDeployVersion.getIsSell())){
//            throw new RequestException();
//        }
//        strategyOrder.setPrice(registerDeployVersion.getPrice());
//
//        User buyer = userCoinService.getUserCoin(strategyOrder.getUserId());
//        if (buyer == null) {
//            throw new RequestException();
//        }
//
//        if (buyer.getAmount() < registerDeployVersion.getPrice()) {
//            throw new RequestException();
//        }
//        float buyerAmount = buyer.getAmount();
//        float diffPrice = registerDeployVersion.getPrice() * -1;
//        buyer = userCoinService.updateAmount(buyer.getUserId(), diffPrice);
//
//        if (buyer.getAmount() != (buyerAmount + diffPrice)) {
//            throw new OperationException("Low balance");
//        }
//
//        oldStrategyDeployService.insertSellHistory(strategyOrder);
//
//        return StrategyStatusService.registerStatus(strategyOrder);
//    }
//
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
//}