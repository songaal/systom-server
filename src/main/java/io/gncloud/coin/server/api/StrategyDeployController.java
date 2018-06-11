package io.gncloud.coin.server.api;

import io.gncloud.coin.server.exception.AbstractException;
import io.gncloud.coin.server.exception.AuthenticationException;
import io.gncloud.coin.server.exception.OperationException;
import io.gncloud.coin.server.exception.RequestException;
import io.gncloud.coin.server.model.StrategyDeploy;
import io.gncloud.coin.server.service.StrategyDeployService;
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
@RequestMapping("/v1/strategies/{strategyId}/versions")
public class StrategyDeployController extends AbstractController{

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(StrategyDeployController.class);

    @Autowired
    private StrategyDeployService strategyDeployService;

    @PostMapping
    public ResponseEntity<?> insertStrategyDeployVersion (@RequestAttribute String userId,
                                                          @PathVariable("strategyId") Integer strategyId,
                                                          @RequestBody StrategyDeploy deploy) {
        try {
            deploy.setId(strategyId);
            deploy.setUserId(userId);
            StrategyDeploy register = strategyDeployService.insertDeployVersion(deploy);
            return success(register);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t){
            logger.error("", t);
            return new OperationException(t.getMessage()).response();
        }
    }

    @GetMapping
    public ResponseEntity<?> selectStrategyDeployVersions (@PathVariable("strategyId") Integer strategyId) {
        try {
            List<StrategyDeploy> registerVersions = strategyDeployService.selectDeployVersions(strategyId);
            return success(registerVersions);
        } catch (Throwable t){
            logger.error("", t);
            return new OperationException(t.getMessage()).response();
        }
    }

    @GetMapping("/{version}")
    public ResponseEntity<?> selectStrategyDeployVersion (@PathVariable("strategyId") Integer strategyId,
                                                          @PathVariable("version") Integer version,
                                                          @RequestAttribute("userId") String userId) {
        try {
            StrategyDeploy registerVersion = strategyDeployService.getDeployVersion(strategyId, version, userId);
            return success(registerVersion);
        } catch (Throwable t){
            logger.error("", t);
            return new OperationException(t.getMessage()).response();
        }
    }

    @DeleteMapping("/{version}")
    public ResponseEntity<?> deleteStrategyDeployVersion (@PathVariable("strategyId") Integer strategyId,
                                                          @PathVariable("version") Integer version,
                                                          @RequestAttribute String userId) {
        try {
            StrategyDeploy registerVersion = strategyDeployService.deleteDeployVersion(userId, strategyId, version);
            if (registerVersion == null) {
                return new RequestException().response();
            }
            return success(registerVersion);
        } catch (Throwable t){
            logger.error("", t);
            return new OperationException(t.getMessage()).response();
        }
    }

    @PostMapping("/{version}/saveBacktest")
    public ResponseEntity<?> saveBackTest (@PathVariable("strategyId") Integer strategyId,
                                           @PathVariable("version") Integer version,
                                           @RequestAttribute("userId") String userId,
                                           @RequestBody StrategyDeploy strategyDeploy) {
        try {
            strategyDeploy.setVersion(version);
            strategyDeploy.setId(strategyId);
            strategyDeploy.setUserId(userId);
            StrategyDeploy registerStrategyDeploy = strategyDeployService.saveBackTest(strategyDeploy);
            return success(registerStrategyDeploy);
        } catch (AuthenticationException e) {
            logger.error("", e);
            return e.response();
        } catch (OperationException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("", t);
            return new OperationException(t.getMessage()).response();
        }
    }



}