package io.systom.coin.api;

import io.systom.coin.exception.AbstractException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.model.Goods;
import io.systom.coin.model.InvestGoods;
import io.systom.coin.model.InvestGoodsCommission;
import io.systom.coin.service.InvestGoodsService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * create joonwoo 2018. 7. 6.
 * 
 */
@RestController
@RequestMapping("/v1/investGoods")
public class InvestGoodsController extends AbstractController{
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    private InvestGoodsService investGoodsService;

    @PostMapping
    public ResponseEntity<?> registerInvestor(@RequestAttribute String userId,
                                              @RequestBody InvestGoods investor) {
        try {
            investor.setUserId(userId);
            InvestGoods investGoods = investGoodsService.registerInvestor(investor);
            return success(investGoods);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Throwable: ", t);
            return new OperationException().response();
        }
    }

    @GetMapping
    public ResponseEntity<?> retrieveInvestGoods(@RequestAttribute String userId) {
        try {
            List<Goods> goodsList = investGoodsService.retrieveInvestGoods(userId);
            return success(goodsList);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Throwable: ", t);
            return new OperationException().response();
        }
    }


    @GetMapping("/{investId}")
    public ResponseEntity<?> getInvestGoodsDetail(@RequestAttribute String userId,
                                            @PathVariable Integer investId) {
        try {
            Goods registerGoodsDetail = investGoodsService.getInvestGoodsDetail(investId, userId);
            String exchangeKeyName = investGoodsService.getExchangeKeyName(investId);
            Map<String, Object> response = new HashMap<>();
            response.put("goodsDetail", registerGoodsDetail);
            response.put("exchangeKeyName", exchangeKeyName);
            return success(response);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Throwable: ", t);
            return new OperationException().response();
        }
    }

    @GetMapping("/{investId}/actions")
    public ResponseEntity<?> investAction(@PathVariable Integer investId,
                                          @RequestAttribute String userId,
                                          @RequestParam String action) {
        try {
            if ("CLOSE_CALCULATION".equalsIgnoreCase(action)) {
                InvestGoodsCommission commission = investGoodsService.calculateCommission(investId);
                return success(commission);
            } else if ("CLOSE_INVEST".equalsIgnoreCase(action)) {
                investGoodsService.removeInvestor(investId, userId);
                return success();
            }
            return success();
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Throwable: ", t);
            return new OperationException().response();
        }
    }

    @DeleteMapping("/{investId}")
    public ResponseEntity<?> removeInvestor(@PathVariable Integer investId,
                                            @RequestAttribute String userId) {
        try {
            InvestGoods investGoods = investGoodsService.removeInvestor(investId, userId);
            return success(investGoods);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Throwable: ", t);
            return new OperationException().response();
        }
    }


}