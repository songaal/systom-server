//package io.gncloud.coin.server.api;
//
//import AbstractException;
//import AuthenticationException;
//import OperationException;
//import RequestException;
//import io.gncloud.coin.server.model.OldStrategyDeploy;
//import io.gncloud.coin.server.service.OldStrategyDeployService;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
///*
// * create joonwoo 2018. 6. 4.
// *
// */
//
//@RestController
//@RequestMapping("/v1/strategies/{strategyId}/versions")
//public class OldStrategyDeployController extends AbstractController{
//
//    private static org.slf4j.Logger logger = LoggerFactory.getLogger(OldStrategyDeployController.class);
//
//    @Autowired
//    private OldStrategyDeployService strategyDeployService;
//
//    @PostMapping
//    public ResponseEntity<?> insertStrategyDeployVersion (@RequestAttribute String userId,
//                                                          @PathVariable("strategyId") Integer strategyId,
//                                                          @RequestBody OldStrategyDeploy deploy) {
//        try {
//            deploy.setId(strategyId);
//            deploy.setUserId(userId);
//            OldStrategyDeploy register = strategyDeployService.insertDeployVersion(deploy);
//            return success(register);
//        } catch (AbstractException e) {
//            logger.error("", e);
//            return e.response();
//        } catch (Throwable t){
//            logger.error("", t);
//            return new OperationException(t.getMessage()).response();
//        }
//    }
//
//    @GetMapping
//    public ResponseEntity<?> selectStrategyDeployVersions (@PathVariable("strategyId") Integer strategyId) {
//        try {
//            List<OldStrategyDeploy> registerVersions = strategyDeployService.selectDeployVersions(strategyId);
//            return success(registerVersions);
//        } catch (Throwable t){
//            logger.error("", t);
//            return new OperationException(t.getMessage()).response();
//        }
//    }
//
//    @GetMapping("/{version}")
//    public ResponseEntity<?> selectStrategyDeployVersion (@PathVariable("strategyId") Integer strategyId,
//                                                          @PathVariable("version") Integer version,
//                                                          @RequestAttribute("userId") String userId) {
//        try {
//            OldStrategyDeploy registerVersion = strategyDeployService.getDeployVersion(strategyId, version, userId);
//            return success(registerVersion);
//        } catch (Throwable t){
//            logger.error("", t);
//            return new OperationException(t.getMessage()).response();
//        }
//    }
//
//    @DeleteMapping("/{version}")
//    public ResponseEntity<?> deleteStrategyDeployVersion (@PathVariable("strategyId") Integer strategyId,
//                                                          @PathVariable("version") Integer version,
//                                                          @RequestAttribute String userId) {
//        try {
//            OldStrategyDeploy registerVersion = strategyDeployService.deleteDeployVersion(userId, strategyId, version);
//            if (registerVersion == null) {
//                return new RequestException().response();
//            }
//            return success(registerVersion);
//        } catch (Throwable t){
//            logger.error("", t);
//            return new OperationException(t.getMessage()).response();
//        }
//    }
//
//    @PostMapping("/{version}/saveBacktest")
//    public ResponseEntity<?> saveBackTest (@PathVariable("strategyId") Integer strategyId,
//                                           @PathVariable("version") Integer version,
//                                           @RequestAttribute("userId") String userId,
//                                           @RequestBody OldStrategyDeploy strategyDeploy) {
//        try {
//            strategyDeploy.setVersion(version);
//            strategyDeploy.setId(strategyId);
//            strategyDeploy.setUserId(userId);
//            OldStrategyDeploy registerStrategyDeploy = strategyDeployService.saveBackTest(strategyDeploy);
//            return success(registerStrategyDeploy);
//        } catch (AuthenticationException e) {
//            logger.error("", e);
//            return e.response();
//        } catch (OperationException e) {
//            logger.error("", e);
//            return e.response();
//        } catch (Throwable t) {
//            logger.error("", t);
//            return new OperationException(t.getMessage()).response();
//        }
//    }
//
//}