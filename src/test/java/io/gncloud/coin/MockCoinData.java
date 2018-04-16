package io.gncloud.coin;

/*
 * create joonwoo 2018. 4. 16.
 * 
 */

import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public class MockCoinData {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(MockCoinData.class);
    private final String streamUrl = "https://7n1h9ritua.execute-api.ap-northeast-2.amazonaws.com/v1/streams/order_stream/record";


//    payload = {
//        'arena': context.sim_params.arena, #backtest | live
//        'agentId': task_config['agentId'],
//                'testId': task_config['testId'],
//                'strategyId': task_config['strategyId'],
//                'user': task_config['user'],
//                'price': price,
//                'orders': orders,
//    }
    @Test
    public void fill() {
        boolean isLive = true;
        Integer agentId = 11;
        Integer strategyId = 54;
        String user = "testuser";

        Date startTime = new Date(2018, 01, 01);
        Date endTime = new Date(2018, 01, 10);
        int interval = 1;
        // 1 시간

        RestTemplate restTemplate = new RestTemplate();

        while (startTime.getTime() < endTime.getTime()) {
//            startTime.setHours(startTime.getHours() + interval);
            startTime.setDate(startTime.getDate() + 1);
            double price = Math.random();
            Map<String, Object> priceMap = randomPrice(startTime, price);

            List<Map<String, Object>> orders = new ArrayList<>();
//            무조건 1개씩 매수
            orders.add(randomOrder(startTime, price));

            Map<String, Object> payload = new HashMap<>();
            payload.put("mode", isLive ? "live" : "backtest");
            payload.put("agentId", agentId);
            payload.put("strategyId", strategyId);
            payload.put("user", user);
            payload.put("price", priceMap);
            payload.put("orders", orders);

            Map<String, Object> request = new HashMap<>();
            request.put("Data", payload);
            request.put("PartitionKey", startTime.getTime());

            HttpEntity httpEntity = new HttpEntity(request, new HttpHeaders());
            logger.info("response : {}", restTemplate.exchange(streamUrl, HttpMethod.PUT, httpEntity, String.class));
        }
    }

//    price = {
//        'timestamp': data.current_dt.value,
//                'exchange': algo_options['exchange'],
//                'coin': algo_options['symbol'].split("_")[0],
//                'price': data.current(context.asset, 'price'),
//                'base': algo_options['symbol'].split("_")[1],
//                'indicator': context.recorded_vars,
//    }
    private Map<String, Object> randomPrice(Date nowTime, Double price) {
        Map<String, Object> priceMap = new HashMap<>();
        priceMap.put("timestamp", nowTime.getTime());
        priceMap.put("exchange", "poloniex");
        priceMap.put("coin", "btc");
        priceMap.put("price", price);
        priceMap.put("base", "usdt");
        priceMap.put("indicator", new HashMap<>());
        return priceMap;
    }

    //    order = {
//        'id': new_order.id,
//                'timestamp': new_order.created.value,
//                'exchange': new_order.asset.exchange,
//                'coin': new_order.asset.quote_currency,
//                'base': new_order.asset.base_currency,
//                'amount': new_order.amount,
//                'price': data.current(context.asset, 'price'),
//                'fee': 0,
//    }
    private Map<String, Object> randomOrder(Date nowTime, Double price) {
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("id", String.valueOf(Math.random() * 1000));
        orderMap.put("timestamp", nowTime.getTime());
        orderMap.put("exchange", "poloniex");
        orderMap.put("coin", "btc");
        orderMap.put("base", "usdt");
        orderMap.put("price", price);
        orderMap.put("amount", 1);
        orderMap.put("fee", 0);
        return orderMap;
    }





}