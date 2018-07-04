package io.systom.coin.api;

import io.systom.coin.exception.AbstractException;
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
public class InvestGoodsController extends AbstractController{
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(InvestGoodsController.class);

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
                                                      @RequestParam(required = false) String types) {
        try {
            List<Goods> registerRecruitGoodsList = new ArrayList<>();
            long nowTime = System.currentTimeMillis();
            Goods searchGoods = null;
            if (types != null && identityService.isManager(userId)) {
                String[] typeArr = types.split(",");
                if (Arrays.binarySearch(typeArr, GOODS_TYPE.wait) >= 0) {
                    searchGoods = new Goods();
                    searchGoods.setUserId(userId);
                    searchGoods.setExchange(exchange);
                    searchGoods.setInvestStart(nowTime);
                    registerRecruitGoodsList.addAll(investGoodsService.retrieveGoodsList(searchGoods));
                }
                if (Arrays.binarySearch(typeArr, GOODS_TYPE.running) >= 0) {
                    searchGoods = new Goods();
                    searchGoods.setUserId(userId);
                    searchGoods.setExchange(exchange);
                    searchGoods.setInvestStart(nowTime);
                    searchGoods.setInvestEnd(nowTime);
                    registerRecruitGoodsList.addAll(investGoodsService.retrieveGoodsList(searchGoods));
                }
                if (Arrays.binarySearch(typeArr, GOODS_TYPE.close) >= 0) {
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

    @PostMapping("/{id}/invest")
    public ResponseEntity<?> registrationInvestor(@PathVariable Integer id,
                                                  @RequestAttribute String userId,
                                                  @RequestBody InvestGoods investor) {
        try {
            investor.setGoodsId(id);
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

    @DeleteMapping("/{id}/invest")
    public ResponseEntity<?> removeInvestor(@PathVariable Integer id,
                                            @RequestAttribute String userId,
                                            @RequestBody InvestGoods investor) {
        try {
            investor.setGoodsId(id);
            investor.setUserId(userId);
            InvestGoods investGoods = investGoodsService.removeInvestor(investor);
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