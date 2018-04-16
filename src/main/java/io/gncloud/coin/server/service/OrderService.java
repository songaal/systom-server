package io.gncloud.coin.server.service;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * create joonwoo 2018. 4. 16.
 * 
 */
@Service
public class OrderService {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private SqlSession sqlSession;

//    public List<> selectAgentHistory(Agent agent) throws OperationException {
//        logger.debug("Order History");
//        List<Order> orderList = null;
//        try {
//            orderList = sqlSession.selectList("order.selectOrderHistory", agent);
//        } catch (Exception e){
//            logger.error("", e);
//            throw new OperationException("[FAIL] Select Order History");
//        }
//        return orderList;
//    }
}