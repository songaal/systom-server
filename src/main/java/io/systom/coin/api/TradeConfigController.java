package io.systom.coin.api;

import io.systom.coin.config.TradeConfig;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 * create joonwoo 2018. 7. 3.
 * 
 */

@RestController
@RequestMapping("/tradeConfig")
public class TradeConfigController extends AbstractController {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(TradeConfigController.class);

    @Autowired
    private TradeConfig tradeConfig;

    @GetMapping("/exchanges")
    public ResponseEntity<?> exchanges(@RequestParam String type) {
        if ("backTest".equals(type)) {
            return success(tradeConfig.getBackTestExchange());
        } else if ("live".equals(type)) {
            return success(tradeConfig.getLiveExchange());
        } else {
            return success();
        }
    }

}