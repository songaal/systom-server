package io.systom.coin;

import com.amazonaws.services.ecs.model.ClientException;
import com.google.gson.Gson;
import io.systom.coin.model.TaskResult;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/*
 * create joonwoo 2018. 7. 20.
 * 
 */
public class ParseTaskResultTest {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(ParseTaskResultTest.class);

    @Test
    public void parseTaskResult() {
        RestTemplate restTemplate = new RestTemplate();
        TaskResult response = null;
        Map<String, Object> responseMap = null;
        String responseStr = null;
        ResponseEntity<Map> responseEntity = null;

        try {
            responseStr = restTemplate.getForObject("http://localhost:8080/result.json", String.class);
        } catch (ClientException re) {
            logger.error("",re);
        }
        TaskResult taskResult = new Gson().fromJson(responseStr, TaskResult.class);

        logger.debug("taskResult: {}", taskResult);

//        String resultJson = new Gson().toJson(response);
//        TaskResult taskResult = new Gson().fromJson(resultJson, TaskResult.class);
    }
}