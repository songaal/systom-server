package io.systom.coin.service;

import io.systom.coin.exception.OperationException;
import io.systom.coin.model.TraderTaskResult;
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

    public List<TraderTaskResult.Result.Trade> getTradeHistory(int investId) {
        List<TraderTaskResult.Result.Trade> tradeHistory = null;
        try {
            tradeHistory = sqlSession.selectList("tradeHistory.getTradeHistory", investId);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
        return tradeHistory;
    }

    public int insertTradeHistory(Integer investId, List<TraderTaskResult.Result.Trade> tradeHistory) {
        int changeRow = 0;
        int size = tradeHistory.size();
        for (int i=0; i < size; i++) {
            tradeHistory.get(i).setId(investId);
        }
        try {
            changeRow = sqlSession.insert("tradeHistory.insertTradeHistory", tradeHistory);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
        return changeRow;
    }

    public int deleteTradeHistory(Integer investId) {
        int changeRow = 0;
        try {
            changeRow = sqlSession.delete("tradeHistory.deleteTradeHistory", investId);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
        return changeRow;
    }
}