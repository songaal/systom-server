package io.gncloud.coin;

import io.gncloud.coin.server.model.ExchangeKey;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/*
 * create joonwoo 2018. 4. 12.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CoinCloudApplication.class)
public class InsertTest {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(InsertTest.class);
    @Autowired
    private SqlSession sqlSession;

    @Test
    public void insertTest() {
        ExchangeKey exchangeKey = new ExchangeKey();
        exchangeKey.setUserId("joonwoo");
        int result = sqlSession.insert("exchange.insertKey", exchangeKey);
        logger.debug("insert count: {}", result);

        List<ExchangeKey> exchangeKeyList = sqlSession.selectList("exchange.selectKeys", exchangeKey.getUserId());

        logger.debug("생성: {}", exchangeKeyList.get(0).getApiKey());
    }

}