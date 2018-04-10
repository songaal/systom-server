package io.gncloud.coin.server.api;

import com.google.gson.Gson;
import io.gncloud.coin.server.exception.AbstractException;
import io.gncloud.coin.server.model.Strategy;
import io.gncloud.coin.server.service.IdentityService;
import io.gncloud.coin.server.service.StrategyService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * create joonwoo 2018. 3. 22.
 * 
 */
@Controller
@RequestMapping("/v1/strategy")
public class StrategyController extends AbstractController{

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(StrategyController.class);

    @Autowired
    private StrategyService strategyService;

    @Autowired
    private IdentityService identityService;

    private Gson gson = new Gson();

    @GetMapping("/me")
    public ResponseEntity<?> getStrategyList(@RequestHeader(name = "X-coincloud-user-id") String token) {
        try {
            List<Strategy> registerStrategyList = strategyService.findTokenByStrategy(token);
            return success(registerStrategyList);
        } catch (AbstractException e){
            logger.error("", e);
            return e.response();
        }
    }

    @GetMapping("/{strategyId}/doc")
    public ResponseEntity<?> getStrategyAgent(@PathVariable String strategyId, @RequestHeader(name = "X-coincloud-user-id") String token) {
//  TODO docker 접근 권한 추가
        try {
            Strategy registerStrategy = strategyService.getStrategy(token, strategyId);
            Map<String, String> response = new HashMap<>();
            response.put("code", registerStrategy.getCode());
            Map<String, Object> optionMap = new HashMap<>();
            if(registerStrategy.getOptions() != null && !"".equals(registerStrategy.getOptions())){
                List<Map<String, Object>> optionList = gson.fromJson(registerStrategy.getOptions(), List.class);
                int optionSize = optionList.size();
                for (int i=0; i < optionSize; i++) {
                    String key = String.valueOf(optionList.get(i).get("key"));
                    Object value = optionList.get(i).get("value");
                    optionMap.put(key, value);
                }
            }
            response.put("options", gson.toJson(optionMap));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (AbstractException e){
            logger.error("", e);
            return e.response();
        }
    }

    @GetMapping("/{strategyId}")
    public ResponseEntity<?> getStrategy(@PathVariable String strategyId, @RequestHeader(name = "X-coincloud-user-id") String token) {
        try {
            Strategy registerStrategy = strategyService.getStrategy(token, strategyId);
            return new ResponseEntity<>(registerStrategy, HttpStatus.OK);
        } catch (AbstractException e){
            logger.error("", e);
            return e.response();
        }
    }

    @PutMapping("/{strategyId}")
    public ResponseEntity<?> updateStrategy(@PathVariable String strategyId, @RequestHeader(name = "X-coincloud-user-id") String token, @RequestBody Strategy strategy) {
        try {
            strategy.setId(strategyId);
            Strategy registerStrategy = strategyService.updateStrategy(token, strategy);
            return success(registerStrategy);
        } catch (AbstractException e){
            logger.error("", e);
            return e.response();
        }
    }

    @PostMapping
    public ResponseEntity<?> createStrategy(@RequestHeader(name = "X-coincloud-user-id") String token, @RequestBody Strategy createStrategy) {
        try {
            logger.debug("token {}, strategy: {}", token, strategyService);
            Strategy registerStrategy = strategyService.insertStrategy(token, createStrategy);
            return success(registerStrategy);
        } catch (AbstractException e){
            logger.error("", e);
            return e.response();
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteStrategy(@RequestHeader(name = "X-coincloud-user-id") String token, @RequestBody String strategyId) {
        try {
            logger.debug("token {}, strategy: {}", token, strategyId);
            Strategy registerStrategy = strategyService.deleteStrategy(token, strategyId);
            return success(registerStrategy);
        } catch (AbstractException e){
            logger.error("", e);
            return e.response();
        }
    }

}