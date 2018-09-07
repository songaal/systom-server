package io.systom.coin.utils;

import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/*
 * create joonwoo 2018. 9. 7.
 * 
 */

public class CurrencyUtils {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(CurrencyUtils.class);
    private static Map<String, Float> currencyRateMap = Collections.synchronizedMap(new HashMap<String, Float>());

    public static float getCurrencyRate(String currency) {
        return currencyRateMap.get(currency);
    }

    protected static void setCurrencyRate(String currency, float rate) {
        logger.debug("[환율 업데이트] 화폐: {}, 환율:{}", currency, rate);
        currencyRateMap.put(currency, rate);
    }


}