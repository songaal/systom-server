package io.gncloud.coin.server.api;

import io.gncloud.coin.server.exception.OperationException;
import io.gncloud.coin.server.model.StrategyOrder;
import io.gncloud.coin.server.model.StrategyStatus;
import io.gncloud.coin.server.service.StrategyOrderService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 * create joonwoo 2018. 6. 12.
 * 
 */
@RestController
@RequestMapping("/v1/orders")
public class OrderController extends AbstractController{
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private StrategyOrderService strategyOrderService;

    @PostMapping("/strategy")
    public ResponseEntity<?> orderStrategy (@RequestAttribute("userId") String userId,
                                            @RequestBody StrategyOrder strategyOrder) {
        try {
            strategyOrder.setUserId(userId);
            StrategyStatus orderStatus = strategyOrderService.order(strategyOrder);
            return success(orderStatus);
        } catch (OperationException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("", t);
            return new OperationException(t.getMessage()).response();
        }
    }

}