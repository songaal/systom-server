//package io.gncloud.coin.server.service;
//
//import OperationException;
//import ParameterException;
//import io.gncloud.coin.server.model.Agent;
//import io.gncloud.coin.server.model.Order;
//import org.apache.ibatis.session.SqlSession;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
///*
// * create joonwoo 2018. 4. 16.
// *
// */
//@Service
//public class OrderService {
//
//    private static org.slf4j.Logger logger = LoggerFactory.getLogger(OrderService.class);
//
//    @Autowired
//    private SqlSession sqlSession;
//
//    public List<Order> selectOrderHistory(Integer agentId) throws OperationException {
//        logger.debug("Select Order History");
//        List<Order> orderList = null;
//        try {
//            orderList = sqlSession.selectList("order.selectOrderHistory", agentId);
//        } catch (Exception e){
//            logger.error("", e);
//            throw new OperationException("[FAIL] Select Order History");
//        }
//        return orderList;
//    }
//
//    public Order insertOrder(Order order) throws OperationException, ParameterException {
//
//        logger.debug("INSERT Order: {}", order);
//        try {
//            int result = sqlSession.insert("order.insertOrder", order);
//            if(result != 1){
//                throw new OperationException("[FAIL] Insert Failed Order. result count: " + result);
//            }
//            return order;
//        } catch (Exception e){
//            logger.error("", e);
//            throw new OperationException("[FAIL] Insert Failed Order");
//        }
//    }
//    public void deleteOrderHistory(Agent agent) throws OperationException {
//        logger.debug("Delete Order History");
//        List<Order> orderList = null;
//        try {
//            sqlSession.delete("order.deleteOrderHistory", agent);
//        } catch (Exception e){
//            logger.error("", e);
//            throw new OperationException("[FAIL] Select Order History");
//        }
//    }
//}