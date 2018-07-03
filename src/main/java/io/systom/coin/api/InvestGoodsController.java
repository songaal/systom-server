package io.systom.coin.api;

import io.systom.coin.exception.AbstractException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.model.Goods;
import io.systom.coin.service.InvestGoodsService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * create joonwoo 2018. 7. 3.
 * 
 */
@RestController
@RequestMapping("/v1/goods")
public class InvestGoodsController extends AbstractController{
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(InvestGoodsController.class);

    @Autowired
    private InvestGoodsService investGoodsService;

    @PostMapping
    public ResponseEntity<?> registerGoods(@RequestAttribute String userId,
                                                 @RequestBody Goods target) {
        try {
            target.setUserId(userId);
            Goods registerGoods = investGoodsService.registerInvestGoods(target);
            return success(registerGoods);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Throwable: ", t);
            return new OperationException().response();
        }
    }

    @GetMapping
    public ResponseEntity<?> retrieveRecruitGoodsList(@RequestParam String exchange) {
        try {
            long nowTime = System.currentTimeMillis();
            List<Goods> registerRecruitGoodsList = investGoodsService.retrieveRecruitGoodsList(nowTime, nowTime, exchange, true);
            return success(registerRecruitGoodsList);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Throwable: ", t);
            return new OperationException().response();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecruitGoods(@PathVariable Integer id) {
        try {
            Goods registerGoods = investGoodsService.getRecruitGoods(id);
            return success(registerGoods);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Throwable: ", t);
            return new OperationException().response();
        }
    }

    @PutMapping("/{id}/hide")
    public ResponseEntity<?> updateRecruitGoodsHide(@RequestAttribute String userId,
                                             @PathVariable Integer id) {
        try {
            Goods registerGoods = investGoodsService.updateRecruitGoodsHide(id, userId);
            return success(registerGoods);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Throwable: ", t);
            return new OperationException().response();
        }
    }

    @PutMapping("/{id}/show")
    public ResponseEntity<?> updateRecruitGoodsShow(@RequestAttribute String userId,
                                                    @PathVariable Integer id) {
        try {
            Goods registerGoods = investGoodsService.updateRecruitGoodsShow(id, userId);
            return success(registerGoods);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Throwable: ", t);
            return new OperationException().response();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecruitGoods(@PathVariable Integer id,
                                                @RequestBody Goods target,
                                                @RequestAttribute String userId) {
        try {
            target.setId(id);
            target.setUserId(userId);
            Goods registerGoods = investGoodsService.updateRecruitGoods(target);
            return success(registerGoods);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Throwable: ", t);
            return new OperationException().response();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecruitGoods(@RequestAttribute String userId,
                                                @PathVariable Integer id) {
        try {
            Goods deleteRecruitGoods = investGoodsService.deleteInvestGoods(id, userId);
            return success(deleteRecruitGoods);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Throwable: ", t);
            return new OperationException().response();
        }
    }


}