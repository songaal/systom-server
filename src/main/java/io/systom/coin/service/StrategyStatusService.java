//package io.gncloud.coin.server.service;
//
//import OperationException;
//import ParameterException;
//import io.gncloud.coin.server.model.StrategyOrder;
//import io.gncloud.coin.server.model.StrategyStatus;
//import org.apache.ibatis.session.SqlSession;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
///*
// * create joonwoo 2018. 6. 12.
// *
// */
//@Service
//public class StrategyStatusService {
//    private static org.slf4j.Logger logger = LoggerFactory.getLogger(StrategyStatusService.class);
//
//    @Autowired
//    private SqlSession sqlSession;
//
//    public StrategyStatus registerStatus(StrategyOrder strategyOrder) throws OperationException, ParameterException {
//        isNotNull(strategyOrder.getUserId(), "userId");
//        isNotNull(strategyOrder.getId(), "strategyId");
//        isNotNull(strategyOrder.getVersion(), "version");
//        try {
//            StrategyStatus status = getStatus(strategyOrder.getUserId(), strategyOrder.getId(), strategyOrder.getVersion());
//            int rowCount;
//            if (status != null) {
//                rowCount = sqlSession.update("strategyStatus.updateStatus", status);
//            } else {
//                rowCount = sqlSession.insert("strategyStatus.insertStatus", strategyOrder);
//            }
//            if (rowCount != 1) {
//                logger.error("update status row count : " + rowCount);
//                throw new OperationException();
//            }
//            return getStatus(strategyOrder.getUserId(), strategyOrder.getId(), strategyOrder.getVersion());
//        } catch (Exception e){
//            logger.error("", e);
//            throw new ParameterException();
//        }
//    }
//
//    public StrategyStatus getStatus(String userId, Integer strategyId, Integer version) {
//        StrategyStatus status = new StrategyStatus();
//        status.setUserId(userId);
//        status.setId(strategyId);
//        status.setVersion(version);
//        return sqlSession.selectOne("strategyStatus.getStatus", status);
//    }
//
//    @Scheduled(fixedDelay = 6000)
//    private void checkExpiration() {
////        List<StrategyStatus> statusList = sqlSession.selectList("strategyStatus.retrieveUsedStatus");
////        int statusSize = statusList.size();
////        long nowTime = new Date().getTime();
////        for (int i=0; i < statusSize; i++){
////            StrategyStatus status = statusList.get(i);
////            Calendar orderTime = Calendar.getInstance();
////            orderTime.setTime(status.getTime());
////            orderTime.add(Calendar.MONTH, 1);
////            long expireTime = orderTime.getTime().getTime();
////            if (expireTime <= nowTime){
////                logger.info("ExpireStatus >> Status: {}", status);
////                sqlSession.update("strategyStatus.updateUnusedStatus", status);
////            }
////        }
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
//    private void isNotNull(Float field, String label) throws ParameterException {
//        if(field == null){
//            throw new ParameterException(label);
//        }
//    }
//}