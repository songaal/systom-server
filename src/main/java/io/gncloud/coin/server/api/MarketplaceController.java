package io.gncloud.coin.server.api;

import io.gncloud.coin.server.exception.AbstractException;
import io.gncloud.coin.server.exception.OperationException;
import io.gncloud.coin.server.model.StrategyDeploy;
import io.gncloud.coin.server.service.MarketplaceService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * create joonwoo 2018. 6. 7.
 * 
 */
@RestController
@RequestMapping("/marketplace")
public class MarketplaceController extends AbstractController {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(MarketplaceController.class);

    @Autowired
    private MarketplaceService marketplaceService;

    @GetMapping
    public ResponseEntity<?> retrieveStrategyMarketList () {
        try {
            List<StrategyDeploy> strategyDeployList = marketplaceService.retrieveStrategyMarketList();
            return success(strategyDeployList);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("", t);
            return new OperationException(t.getMessage()).response();
        }
    }

    @PutMapping("/register")
    public ResponseEntity<?> registerStrategy (@RequestAttribute String userId,
                                               @RequestBody StrategyDeploy strategyDeploy) {
        strategyDeploy.setUserId(userId);
        try {
            StrategyDeploy registeredStrategyVersion = marketplaceService.registerStrategyMarket(strategyDeploy);
            return success(registeredStrategyVersion);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("", t);
            return new OperationException(t.getMessage()).response();
        }
    }

}
