package io.systom.coin.api;

import io.systom.coin.exception.AbstractException;
import io.systom.coin.exception.AuthenticationException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.model.Goods;
import io.systom.coin.model.InvestGoods;
import io.systom.coin.service.IdentityService;
import io.systom.coin.service.InvestGoodsService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * create joonwoo 2018. 7. 3.
 * 
 */
@RestController
@RequestMapping("/v1/goods")
public class GoodsController extends AbstractController{
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    private InvestGoodsService investGoodsService;
    @Autowired
    private IdentityService identityService;

    private enum GOODS_TYPE { wait, running, close }

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
    public ResponseEntity<?> retrieveGoodsList(@RequestAttribute String userId,
                                                      @RequestParam String exchange,
                                                      @RequestParam(required = false) String type) {
        try {
            List<Goods> registerRecruitGoodsList = new ArrayList<>();
            long nowTime = System.currentTimeMillis();
            Goods searchGoods = null;
            if (type != null && identityService.isManager(userId)) {
                List<String> typeList = Arrays.asList(type.split(","));

                if (typeList.contains(GOODS_TYPE.wait.name())) {
                    logger.debug("retrieveGoodsList wait");
                    searchGoods = new Goods();
                    searchGoods.setUserId(userId);
                    searchGoods.setExchange(exchange);
                    searchGoods.setInvestStart(nowTime);
                    registerRecruitGoodsList.addAll(investGoodsService.retrieveGoodsList(searchGoods));
                }
                if (typeList.contains(GOODS_TYPE.running.name())) {
                    logger.debug("retrieveGoodsList running");
                    searchGoods = new Goods();
                    searchGoods.setUserId(userId);
                    searchGoods.setExchange(exchange);
                    searchGoods.setInvestStart(nowTime);
                    searchGoods.setInvestEnd(nowTime);
                    registerRecruitGoodsList.addAll(investGoodsService.retrieveGoodsList(searchGoods));
                }
                if (typeList.contains(GOODS_TYPE.close.name())) {
                    logger.debug("retrieveGoodsList close");
                    searchGoods = new Goods();
                    searchGoods.setUserId(userId);
                    searchGoods.setExchange(exchange);
                    searchGoods.setInvestEnd(nowTime);
                    registerRecruitGoodsList.addAll(investGoodsService.retrieveGoodsList(searchGoods));
                }
            } else {
                searchGoods = new Goods();
                searchGoods.setRecruitStart(nowTime);
                searchGoods.setRecruitEnd(nowTime);
                searchGoods.setDisplay(true);
                searchGoods.setExchange(exchange);
                searchGoods.setUserId(userId);
                registerRecruitGoodsList = investGoodsService.retrieveGoodsList(searchGoods);
            }
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
    public ResponseEntity<?> getGoods(@RequestAttribute String userId,
                                      @PathVariable Integer id) {
        try {
            Goods registerGoods = investGoodsService.getGoods(id);
            long nowTime = System.currentTimeMillis();
            InvestGoods investGoods = investGoodsService.findInvestIdByUser(id, userId);
            if (investGoods != null) {
                registerGoods.setInvest(true);
            }
            if (!identityService.isManager(userId)
                    && registerGoods.getRecruitStart() > nowTime
                    && registerGoods.getRecruitEnd() < nowTime) {
                throw new AuthenticationException();
            }
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
            Goods deleteRecruitGoods = investGoodsService.deleteGoods(id, userId);
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