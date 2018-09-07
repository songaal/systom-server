package io.systom.coin;

import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

/*
 * create joonwoo 2018. 9. 6.
 * 
 */
public class CurrencyTest {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(CurrencyTest.class);

    @Test
    public void currencyApiTest() {
        String currency = "KRW";
        String url = String.format("https://query1.finance.yahoo.com/v8/finance/chart/%s=X?region=US&lang=en-US&includePrePost=false&interval=1h&range=1d&corsDomain=finance.yahoo.com&.tsrc=finance", currency);
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(url, String.class);
        float rate = Float.parseFloat(JsonPath.read(result, "$.chart.result[0].meta.previousClose").toString());
        logger.info("rate: {}", rate);

    }

}