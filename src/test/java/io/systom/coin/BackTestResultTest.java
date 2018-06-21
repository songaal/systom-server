package io.systom.coin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.systom.coin.model.BackTestResult;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
 * create joonwoo 2018. 6. 21.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class BackTestResultTest {
    private org.slf4j.Logger logger = LoggerFactory.getLogger(BackTestResult.class);

    @Autowired
    private SqlSession sqlSession;


    public BackTestResult getResultData() {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject("http://localhost:8080/result.json", String.class);
        return new Gson().fromJson(response, BackTestResult.class);

    }


    @Test
    public void saveBackTestResult() {
        BackTestResult backTestResult = getResultData();

        logger.debug("=========== backtest result parse ===========");
        logger.debug("Request: {}", backTestResult.getRequest());
        logger.debug("trade history: {}", backTestResult.getResult().getTradeHistory());

        int investGoodsId = 0;
        recordBackTestPerformance(investGoodsId, backTestResult.getResult());
        recordBackTestTradeHistory(investGoodsId, backTestResult.getResult().getTradeHistory());
        recordBackTestValueHistory(investGoodsId, backTestResult.getResult().getEquity(), backTestResult.getResult().getCumReturns(), backTestResult.getResult().getDrawdowns());
    }

    public void recordBackTestPerformance(int investGoodsId, BackTestResult.Result result) {
        result.setId(investGoodsId);
        int changeRow = sqlSession.insert("backtest.recordPerformance", result);
        logger.debug("recordPerformance row: {}", changeRow);
    }

    public void recordBackTestTradeHistory(int investGoodsId, List<BackTestResult.Result.Trade> trades) {
        trades.forEach(trade -> {
            trade.setId(investGoodsId);
        });
        int changeRow = sqlSession.insert("backtest.recordTradeHistory", trades);
        logger.debug("recordTradeHistory row: {}", changeRow);
    }

    public void recordBackTestValueHistory(int investGoodsId, Map<Long, Float> equities, Map<Long, Float> cumReturns, Map<Long, Float> drawdowns){

        List<BackTestResult.Result.Value> values = new ArrayList<>();
        Iterator<Map.Entry<Long, Float>> iterator = equities.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Float> entry = iterator.next();
            Long ts = entry.getKey();
            BackTestResult.Result.Value v = new BackTestResult.Result.Value();
            v.setId(investGoodsId);
            v.setTimestamp(ts);
            v.setEquity(entry.getValue());
            if (cumReturns.get(ts) != null) {
                v.setCumReturn(cumReturns.get(ts));
                cumReturns.remove(ts);
            }
            if (drawdowns.get(ts) != null) {
                v.setDrawdown(drawdowns.get(ts));
                drawdowns.remove(ts);
            }
            values.add(v);
        }

        iterator = cumReturns.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<Long, Float> entry = iterator.next();
            Long ts = entry.getKey();
            BackTestResult.Result.Value v = new BackTestResult.Result.Value();
            v.setId(investGoodsId);
            v.setTimestamp(ts);
            v.setCumReturn(cumReturns.get(ts));
            if (drawdowns.get(ts) != null) {
                v.setDrawdown(drawdowns.get(ts));
                drawdowns.remove(ts);
            }
            values.add(v);
        }

        iterator = drawdowns.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<Long, Float> entry = iterator.next();
            Long ts = entry.getKey();
            BackTestResult.Result.Value v = new BackTestResult.Result.Value();
            v.setId(investGoodsId);
            v.setTimestamp(ts);
            v.setDrawdown(drawdowns.get(ts));
            values.add(v);
        }
        int changeRow = sqlSession.insert("backtest.recordValueHistory", values);
        logger.debug("recordValueHistory row: {}", changeRow);
    }






    @Test
    public void testGet() {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject("http://localhost:8080/result.json", String.class);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        BackTestResult backTestResult = new Gson().fromJson(response, BackTestResult.class);
        logger.info("trade history: {}", backTestResult.getResult().getTradeHistory());


    }
}