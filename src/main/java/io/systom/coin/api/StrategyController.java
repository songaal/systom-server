package io.systom.coin.api;

import io.systom.coin.exception.AbstractException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.model.Strategy;
import io.systom.coin.service.StrategyService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/*
 * create joonwoo 2018. 6. 20.
 * 
 */
@RestController
@RequestMapping("/v1/strategies")
public class StrategyController extends AbstractController {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(StrategyController.class);

    @Autowired private StrategyService strategyService;

    @PostMapping
    public ResponseEntity<?> createBlankStrategy(@RequestAttribute String userId, @RequestBody Strategy strategy) {
        try {
            logger.debug("createBlankStrategy > {}", strategy);
            strategy.setUserId(userId);
            Strategy newStrategy = strategyService.createBlankStrategy(strategy);
            return success(newStrategy);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("", t);
            return new OperationException(t.getMessage()).response();
        }
    }

    @GetMapping
    public ResponseEntity<?> retrieveStrategiesByUserId(@RequestAttribute String userId) {
        try {
            logger.debug("retrieveStrategiesByUserId > userId: {}", userId);
            List<Strategy> registerStrategies = strategyService.retrieveStrategiesByUserId(userId);
            return success(registerStrategies);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Fatal error: ", t);
            return new OperationException().response();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStrategy(@RequestAttribute String userId, @PathVariable Integer id) {
        try {
            logger.debug("getStrategy > id: {}, userId: {}", id, userId);
            Strategy registerStrategy = strategyService.findStrategyById(id, userId);
            return new ResponseEntity<>(registerStrategy, HttpStatus.OK);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Fatal error: ", t);
            return new OperationException().response();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStrategy(@RequestAttribute String userId, @PathVariable Integer id, @RequestBody Strategy target) {
        try {
            logger.debug("updateStrategy > id: {}, userId: {}", id, userId);
            target.setId(id);
            target.setUserId(userId);
            Strategy updatedStrategy = strategyService.updateStrategy(target);
            return success(updatedStrategy);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("", t);
            return new OperationException(t.getMessage()).response();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStrategy(@RequestAttribute String userId, @PathVariable Integer id) {
        try {
            logger.debug("deleteStrategy > id: {}, userId: {}", id, userId);
            Strategy deletedStrategy = strategyService.deleteStrategy(new Strategy(id, userId));
            return success(deletedStrategy);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("", t);
            return new OperationException(t.getMessage()).response();
        }
    }
}