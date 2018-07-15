package io.systom.coin.api;

import io.systom.coin.exception.AbstractException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.model.Strategy;
import io.systom.coin.model.StrategyDeployVersion;
import io.systom.coin.service.StrategyDeployService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * create joonwoo 2018. 6. 4.
 * 
 */

@RestController
@RequestMapping("/v1/strategies/{id}/versions")
public class StrategyDeployController extends AbstractController{

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(StrategyDeployController.class);

    @Autowired
    private StrategyDeployService strategyDeployService;

    @PostMapping
    public ResponseEntity<?> createDeployVersion (@RequestAttribute String userId,
                                                  @PathVariable Integer id,
                                                  @RequestBody StrategyDeployVersion newVersion) {
        logger.debug("createDeployVersion > id: {}, userId: {}", id, userId);
        try {
            newVersion.setId(id);
            newVersion.setUserId(userId);
            List<StrategyDeployVersion> registerDeployVersion = strategyDeployService.createDeployVersion(newVersion);
            return success(registerDeployVersion);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t){
            logger.error("", t);
            return new OperationException(t.getMessage()).response();
        }
    }

    @GetMapping
    public ResponseEntity<?> retrieveDeployVersions (@PathVariable Integer id) {
        logger.debug("retrieveDeployVersions > id: {}", id);
        try {
            Strategy strategy = new Strategy();
            strategy.setId(id);
            List<StrategyDeployVersion> registerVersions = strategyDeployService.retrieveDeployVersions(strategy);
            return success(registerVersions);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t){
            logger.error("", t);
            return new OperationException(t.getMessage()).response();
        }
    }

    @GetMapping("/{version}")
    public ResponseEntity<?> getDeployVersion (@PathVariable Integer id,
                                               @PathVariable Integer version,
                                               @RequestAttribute String userId) {
        logger.debug("getDeployVersion > id: {}, version: {}, user_id: {}", id, version, userId);
        try {
            StrategyDeployVersion target = new StrategyDeployVersion(id, userId, version);
            target = strategyDeployService.getDeployVersion(target);
            return success(target);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t){
            logger.error("", t);
            return new OperationException().response();
        }

    }

    @DeleteMapping("/{version}")
    public ResponseEntity<?> deleteStrategyDeployVersion (@PathVariable Integer id,
                                                          @PathVariable Integer version,
                                                          @RequestAttribute String userId) {
        logger.debug("deleteStrategyDeployVersion > id: {}, version: {}, user_id: {}", id, version, userId);

        try {
            StrategyDeployVersion target = new StrategyDeployVersion(id, userId, version);
            target = strategyDeployService.deleteDeployVersion(target);
            return success(target);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t){
            logger.error("", t);
            return new OperationException().response();
        }
    }

    @GetMapping("/{version}/model")
    public ResponseEntity<?> getStrategyModel (@PathVariable Integer id,
                                               @PathVariable(required = false) Integer version) {
        logger.debug("getDeployVersion > id: {}, version: {}, user_id: {}", id, version);
        try {
            String model = strategyDeployService.getStrategyModel(id, version);
            return success(model);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t){
            logger.error("", t);
            return new OperationException().response();
        }

    }
}