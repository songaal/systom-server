package io.systom.coin.service;

import io.systom.coin.exception.OperationException;
import io.systom.coin.model.ExchangeKey;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * create joonwoo 2018. 4. 12.
 * 
 */
@Service
public class ExchangeService {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(ExchangeService.class);

    @Autowired private SqlSession sqlSession;

    public List<ExchangeKey> selectExchangeKeys(String userId) throws OperationException {
        try {
            return sqlSession.selectList("exchange.selectKeys", userId);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] select User Exchange Key");
        }
    }

    public ExchangeKey selectExchangeKey(ExchangeKey exchangeKey) throws OperationException {
        try {
            return sqlSession.selectOne("exchange.selectKey", exchangeKey);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] select User Exchange Key");
        }
    }

    public ExchangeKey insertExchangeKey (ExchangeKey exchangeKey) throws OperationException {
        try {
            int resultCount = sqlSession.insert("exchange.insertKey", exchangeKey);
            if (resultCount != 1) {
                throw new OperationException("[FAIL] insert exchangeKey result 0");
            }
            return exchangeKey;
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] insert exchangeKey error");
        }
    }

    public ExchangeKey deleteExchangeKey (ExchangeKey exchangeKey) throws OperationException {
        try {
            int resultCount = sqlSession.delete("exchange.deleteKey", exchangeKey);
            if (resultCount != 1) {
                throw new OperationException("[FAIL] delete exchangeKey result 0");
            }
            return exchangeKey;
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] delete exchangeKey error");
        }
    }


}