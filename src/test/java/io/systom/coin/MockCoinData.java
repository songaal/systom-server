package io.systom.coin;

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
    private Calendar calendar = Calendar.getInstance();
    private RestTemplate restTemplate = new RestTemplate();

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
        boolean isLive = false;
        Integer taskId = 346;
        Integer strategyId = 59;
        String user = "testuser";

        calendar.set(2018, 00, 01, 00, 00, 00);
        Date startTime = calendar.getTime();
        calendar.set(2018, 00, 01, 01, 00, 00);
        Date endTime = calendar.getTime();

        int interval = 1;
        String intervalUnit = "T";

        while (startTime.getTime() < endTime.getTime()) {
            if ("T".equalsIgnoreCase(intervalUnit)) {
                startTime.setMinutes(startTime.getMinutes() + interval);
            } else if ("H".equalsIgnoreCase(intervalUnit)) {
                startTime.setHours(startTime.getHours() + interval);
            } else {
                startTime.setDate(startTime.getDate() + interval);
            }

            long timestamp = startTime.getTime();
            timestamp = timestamp * 1000000l;
//            timestamp = 1515556800000000000l;
            double price = Math.random();
            Map<String, Object> priceMap = randomPrice(timestamp, price);

            List<Map<String, Object>> orders = new ArrayList<>();

            orders.add(randomOrder(timestamp, price));
            orders.add(randomOrder(timestamp, price));

            Map<String, Object> payload = new HashMap<>();
            if (isLive) {
                payload.put("mode", "live");
                payload.put("agentId", taskId);
            }else {
                payload.put("mode", "backtest");
                payload.put("testId", taskId);
            }
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
    private Map<String, Object> randomPrice(long timestamp, Double price) {
        Map<String, Object> priceMap = new HashMap<>();
        priceMap.put("timestamp", timestamp);
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
    private Map<String, Object> randomOrder(long timestamp, Double price) {
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("exchangeOrderId", String.valueOf(Math.random() * 1000));
        orderMap.put("timestamp", timestamp);
        orderMap.put("exchange", "poloniex");
        orderMap.put("coin", "btc");
        orderMap.put("base", "usdt");
        orderMap.put("price", price);
        orderMap.put("desc", "이동 평균");
        if (((int)(Math.random() * 1000) % 2) == 1) {
            orderMap.put("amount", Math.    random());
        } else {
            orderMap.put("amount",Math.random() * -1);
        }
        orderMap.put("fee", 0);
        return orderMap;
    }





}