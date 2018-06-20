//package io.gncloud.coin.server.api;
//
//import AbstractException;
//import OperationException;
//import io.gncloud.coin.server.model.OldStrategy;
//import io.gncloud.coin.server.model.OldStrategyDeploy;
//import IdentityService;
//import io.gncloud.coin.server.service.OldStrategyDeployService;
//import io.gncloud.coin.server.service.OldStrategyService;
//import io.gncloud.coin.server.service.TaskService;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///*
// * create joonwoo 2018. 3. 22.
// *
// */
//@Controller
//@RequestMapping("/v1/strategies")
//public class OldStrategyController extends AbstractController {
//
//    private static org.slf4j.Logger logger = LoggerFactory.getLogger(OldStrategyController.class);
//
//    @Autowired
//    private OldStrategyService strategyService;
//
//    @Autowired
//    private OldStrategyDeployService strategyDeployService;
//
//    @Autowired
//    private IdentityService identityService;
//
//    @Autowired
//    private TaskService taskService;
//
//
//    @GetMapping("/me")
//    public ResponseEntity<?> getStrategyList(@RequestAttribute String userId) {
//        try {
//            List<OldStrategy> registerOldStrategyList = strategyService.retrieveStrategiesByUserId(userId);
//            List<OldStrategyDeploy> registerOrderStrategyList = strategyDeployService.retrieveOrderStrategy(userId);
//            Map<String, Object> response = new HashMap<>();
//            response.put("strategyList", registerOldStrategyList);
//            response.put("orderStrategyList", registerOrderStrategyList);
//            return success(response);
//        } catch (AbstractException e) {
//            logger.error("", e);
//            return e.response();
//        } catch (Throwable t) {
//            logger.error("", t);
//            return new OperationException(t.getMessage()).response();
//        }
//    }
//
//    @GetMapping("/{strategyId}")
//    public ResponseEntity<?> getStrategy(@RequestAttribute String userId, @PathVariable Integer strategyId) {
//        try {
//            OldStrategy registerOldStrategy = strategyService.getStrategy(strategyId, userId);
//            return new ResponseEntity<>(registerOldStrategy, HttpStatus.OK);
//        } catch (AbstractException e) {
//            logger.error("", e);
//            return e.response();
//        } catch (Throwable t) {
//            logger.error("", t);
//            return new OperationException(t.getMessage()).response();
//        }
//    }
//
//    @PutMapping("/{strategyId}")
//    public ResponseEntity<?> updateStrategy(@RequestAttribute String userId, @PathVariable Integer strategyId, @RequestBody OldStrategy oldStrategy) {
//        try {
//            oldStrategy.setId(strategyId);
//            OldStrategy registerOldStrategy = strategyService.updateStrategy(oldStrategy, userId);
//            return success(registerOldStrategy);
//        } catch (AbstractException e) {
//            logger.error("", e);
//            return e.response();
//        } catch (Throwable t) {
//            logger.error("", t);
//            return new OperationException(t.getMessage()).response();
//        }
//    }
//
//    @PostMapping
//    public ResponseEntity<?> createStrategy(@RequestAttribute String userId, @RequestBody OldStrategy createOldStrategy) {
//        try {
//            logger.debug("userId {}, strategy: {}", userId, strategyService);
//            createOldStrategy.setUserId(userId);
//            OldStrategy registerOldStrategy = strategyService.insertStrategy(createOldStrategy);
//            return success(registerOldStrategy);
//        } catch (AbstractException e) {
//            logger.error("", e);
//            return e.response();
//        } catch (Throwable t) {
//            logger.error("", t);
//            return new OperationException(t.getMessage()).response();
//        }
//    }
//
//    @DeleteMapping("/{strategyId}")
//    public ResponseEntity<?> deleteStrategy(@RequestAttribute String userId, @PathVariable Integer strategyId) {
//        try {
//            logger.debug("userId {}, strategy: {}", userId, strategyId);
//            OldStrategy registerOldStrategy = strategyService.deleteStrategy(strategyId, userId);
//            return success(registerOldStrategy);
//        } catch (AbstractException e) {
//            logger.error("", e);
//            return e.response();
//        } catch (Throwable t) {
//            logger.error("", t);
//            return new OperationException(t.getMessage()).response();
//        }
//    }
//
//}