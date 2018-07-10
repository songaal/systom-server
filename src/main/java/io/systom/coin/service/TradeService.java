package io.systom.coin.service;

import io.systom.coin.exception.OperationException;
import io.systom.coin.model.Trade;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * create joonwoo 2018. 7. 10.
 * 
 */
@Service
public class TradeService {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(TradeService.class);

    @Autowired
    private SqlSession sqlSession;

    public List<Trade> getTradeHistory(int investId) {
        List<Trade> tradeHistory = null;
        try {
            tradeHistory = sqlSession.selectList("tradeHistory.getTradeHistory", investId);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
        return tradeHistory;
    }

}