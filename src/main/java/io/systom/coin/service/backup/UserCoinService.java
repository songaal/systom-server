//package io.gncloud.coin.server.service;
//
//import OperationException;
//import ParameterException;
//import User;
//import org.apache.ibatis.session.SqlSession;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
///*
// * create joonwoo 2018. 6. 11.
// *
// */
//@Service
//public class UserCoinService {
//
//    private static org.slf4j.Logger logger = LoggerFactory.getLogger(UserCoinService.class);
//
//    @Autowired
//    private SqlSession sqlSession;
//
//    public void insertUserCoin(String userId) throws OperationException {
//        try {
//            int cnt = sqlSession.insert("userCoin.insertUserCoin", userId);
//            if (cnt != 1) {
//                logger.error("UserCoin insert row: " + cnt);
//                throw new OperationException("UserCoin insert row: " + cnt);
//            }
//        } catch (Exception e) {
//            logger.error("", e);
//            throw new OperationException();
//        }
//    }
//
//    public User updateAmount(String userId, float amount) throws ParameterException, OperationException {
//        isNotNull(userId, "userId");
//        isNotNull(amount, "Amount");
//
//        try {
//            User registeredUser = getUserCoin(userId);
//            if (registeredUser == null) {
//                insertUserCoin(userId);
//            }
//            registeredUser.setAmount(amount);
//            int cnt = sqlSession.update("userCoin.updateAmount", registeredUser);
//            if (cnt != 1) {
//                logger.error("update userCoin count: " + cnt);
//                throw new OperationException();
//            }
//        } catch (Exception e) {
//            logger.error("", e);
//            throw new OperationException();
//        }
//        return getUserCoin(userId);
//    }
//
//    public User getUserCoin(String userId) {
//        return sqlSession.selectOne("userCoin.getUserCoin", userId);
//    }
//
//
//    private void isNotNull(String field, String label) throws ParameterException {
//        if(field == null || "".equals(field)){
//            throw new ParameterException(label);
//        }
//    }
//    private void isNotNull(Float field, String label) throws ParameterException {
//        if(field == null || field == 0.0f){
//            throw new ParameterException(label);
//        }
//    }
//}