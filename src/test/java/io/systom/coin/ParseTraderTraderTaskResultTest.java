package io.systom.coin;

import com.amazonaws.services.ecs.model.ClientException;
import com.google.gson.Gson;
import io.systom.coin.model.TraderTaskResult;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/*
 * create joonwoo 2018. 7. 20.
 * 
 */
public class ParseTraderTraderTaskResultTest {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(ParseTraderTraderTaskResultTest.class);

    @Test
    public void parseTaskResult() {
        RestTemplate restTemplate = new RestTemplate();
        TraderTaskResult response = null;
        Map<String, Object> responseMap = null;
        String responseStr = null;
        ResponseEntity<Map> responseEntity = null;

        try {
            responseStr = restTemplate.getForObject("http://localhost:8080/result.json", String.class);
        } catch (ClientException re) {
            logger.error("",re);
        }
        TraderTaskResult traderTaskResult = new Gson().fromJson(responseStr, TraderTaskResult.class);

        logger.debug("traderTaskResult: {}", traderTaskResult);

//        String resultJson = new Gson().toJson(response);
//        TraderTaskResult traderTaskResult = new Gson().fromJson(resultJson, TraderTaskResult.class);
    }
}