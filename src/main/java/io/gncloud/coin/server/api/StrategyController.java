package io.gncloud.coin.server.api;

import com.google.gson.Gson;
import io.gncloud.coin.server.exception.AbstractException;
import io.gncloud.coin.server.exception.OperationException;
import io.gncloud.coin.server.model.Strategy;
import io.gncloud.coin.server.service.IdentityService;
import io.gncloud.coin.server.service.StrategyService;
import io.gncloud.coin.server.service.TaskService;
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
@RequestMapping("/v1/strategies")
public class StrategyController extends AbstractController {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(StrategyController.class);

    @Autowired
    private StrategyService strategyService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private TaskService taskService;
    private Gson gson = new Gson();

    @GetMapping("/me")
    public ResponseEntity<?> getStrategyList(@RequestAttribute String userId) {
        try {
            List<Strategy> registerStrategyList = strategyService.findStrategyByUser(userId);
            return success(registerStrategyList);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("", t);
            return new OperationException(t.getMessage()).response();
        }
    }

    @GetMapping("/{strategyId}/model")
    public ResponseEntity<?> getStrategyModel(@RequestAttribute String userId, @PathVariable Integer strategyId) {
        try {
            Strategy registerStrategy = taskService.getBackTestModel(strategyId, userId);
            Map<String, String> response = new HashMap<>();
            response.put("code", registerStrategy.getCode());
            response.put("options", registerStrategy.getOptions());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("", t);
            return new OperationException(t.getMessage()).response();
        }
    }

    @GetMapping("/{strategyId}")
    public ResponseEntity<?> getStrategy(@RequestAttribute String userId, @PathVariable Integer strategyId) {
        try {
            Strategy registerStrategy = strategyService.getStrategy(strategyId, userId);
            return new ResponseEntity<>(registerStrategy, HttpStatus.OK);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("", t);
            return new OperationException(t.getMessage()).response();
        }
    }

    @PutMapping("/{strategyId}")
    public ResponseEntity<?> updateStrategy(@RequestAttribute String userId, @PathVariable Integer strategyId, @RequestBody Strategy strategy) {
        try {
            strategy.setId(strategyId);
            Strategy registerStrategy = strategyService.updateStrategy(strategy, userId);
            return success(registerStrategy);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("", t);
            return new OperationException(t.getMessage()).response();
        }
    }

    @PostMapping
    public ResponseEntity<?> createStrategy(@RequestAttribute String userId, @RequestBody Strategy createStrategy) {
        try {
            logger.debug("userId {}, strategy: {}", userId, strategyService);
            createStrategy.setUserId(userId);
            Strategy registerStrategy = strategyService.insertStrategy(createStrategy);
            return success(registerStrategy);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("", t);
            return new OperationException(t.getMessage()).response();
        }
    }

    @DeleteMapping("/{strategyId}")
    public ResponseEntity<?> deleteStrategy(@RequestAttribute String userId, @PathVariable Integer strategyId) {
        try {
            logger.debug("userId {}, strategy: {}", userId, strategyId);
            Strategy registerStrategy = strategyService.deleteStrategy(strategyId, userId);
            return success(registerStrategy);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("", t);
            return new OperationException(t.getMessage()).response();
        }
    }

}