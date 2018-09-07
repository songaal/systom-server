package io.systom.coin.scheduler;

import com.jayway.jsonpath.JsonPath;
import io.systom.coin.utils.CurrencyUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

/*
 * create joonwoo 2018. 9. 7.
 * 
 */
@Component
public class CurrencyRateScheduler extends CurrencyUtils {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(CurrencyRateScheduler.class);

    private final String currencyRateUrl = "https://query1.finance.yahoo.com/v8/finance/chart/%s=X?region=US&lang=en-US&includePrePost=false&interval=1h&range=1d&corsDomain=finance.yahoo.com&.tsrc=finance";

    @Value("${scheduler.isCurrencyRateUpdate}")
    private String isCurrencyRateUpdate;

    @PostConstruct
    public void init() {
        if (!"true".equalsIgnoreCase(isCurrencyRateUpdate)) {
            logger.debug("개발 모드 환율 적용");
            super.setCurrencyRate("KRW", 1100.0f);
            return;
        } else {
            logger.debug("라이브 환율 적용");
            task();
        }

    }

    @Scheduled(cron = "0 0 12 * * *")
    public void task() {
        if (!"true".equalsIgnoreCase(isCurrencyRateUpdate)) {
            logger.debug("isCurrencyRateUpdate Disabled");
            return;
        }
        String currency = "KRW";
        String url = String.format(currencyRateUrl, currency);
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(url, String.class);
        float rate = Float.parseFloat(JsonPath.read(result, "$.chart.result[0].meta.previousClose").toString());
        super.setCurrencyRate(currency, rate);
    }

}