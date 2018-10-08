package io.systom.coin.api;

import io.systom.coin.exception.AbstractException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.model.Goods;
import io.systom.coin.model.InvestGoods;
import io.systom.coin.service.InvestGoodsService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<?> registrationInvestor(@RequestAttribute String userId,
                                                  @RequestBody InvestGoods investor) {
        try {
            investor.setUserId(userId);
            InvestGoods investGoods = investGoodsService.registrationInvestor(investor);
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
            return success(registerGoodsDetail);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Throwable: ", t);
            return new OperationException().response();
        }
    }

    @GetMapping("/{investId}/action")
    public ResponseEntity<?> investAction(@PathVariable Integer investId,
                                          @RequestParam("action") String action) {
        try {
            if ("CLOSE_INVEST".equalsIgnoreCase(action)) {



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