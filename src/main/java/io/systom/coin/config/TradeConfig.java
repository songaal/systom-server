package io.systom.coin.config;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/*
 * create joonwoo 2018. 7. 3.
 * 
 */
@Component
public class TradeConfig {

    private List<String> liveExchange = Arrays.asList("binance", "bithumb");
    public List<String> getLiveExchange() {
        return liveExchange;
    }




}