//package io.gncloud.coin.server.api;
//
//import AbstractException;
//import AuthenticationException;
//import OperationException;
//import io.gncloud.coin.server.model.OldStrategyDeploy;
//import io.gncloud.coin.server.service.MarketplaceService;
//import io.gncloud.coin.server.service.StrategyOrderService;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
///*
// * create joonwoo 2018. 6. 7.
// *
// */
//@RestController
//@RequestMapping("/v1/marketplace")
//public class MarketplaceController extends AbstractController {
//
//    private static org.slf4j.Logger logger = LoggerFactory.getLogger(MarketplaceController.class);
//
//    @Autowired
//    private MarketplaceService marketplaceService;
//
//    @Autowired
//    private StrategyOrderService strategyOrderService;
//
//    @GetMapping
//    public ResponseEntity<?> retrieveStrategyMarketList (@RequestAttribute String userId) {
//        try {
//            List<OldStrategyDeploy> strategyDeployList = marketplaceService.retrieveStrategyMarketList(userId);
//            return success(strategyDeployList);
//        } catch (AbstractException e) {
//            logger.error("", e);
//            return e.response();
//        } catch (Throwable t) {
//            logger.error("", t);
//            return new OperationException(t.getMessage()).response();
//        }
//    }
//
//    @PutMapping("/register")
//    public ResponseEntity<?> registerStrategy (@RequestAttribute String userId,
//                                               @RequestBody OldStrategyDeploy strategyDeploy) {
//        strategyDeploy.setUserId(userId);
//        try {
//            OldStrategyDeploy registeredStrategyVersion = marketplaceService.registerStrategyMarket(strategyDeploy);
//            return success(registeredStrategyVersion);
//        } catch (AbstractException e) {
//            logger.error("", e);
//            return e.response();
//        } catch (Throwable t) {
//            logger.error("", t);
//            return new OperationException(t.getMessage()).response();
//        }
//    }
//
//    @PutMapping("/stopSelling")
//    public ResponseEntity<?> stopSellingStrategyMarket (@RequestAttribute("userId") String userId,
//                                                        @RequestBody OldStrategyDeploy strategyDeploy) {
//        try {
//            strategyDeploy.setUserId(userId);
//            OldStrategyDeploy registerStrategyDeploy = marketplaceService.stopSellingStrategyMarket(strategyDeploy);
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
