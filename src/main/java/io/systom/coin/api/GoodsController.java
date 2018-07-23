package io.systom.coin.api;

import io.systom.coin.exception.AbstractException;
import io.systom.coin.exception.AuthenticationException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.model.Goods;
import io.systom.coin.model.Task;
import io.systom.coin.service.GoodsService;
import io.systom.coin.service.IdentityService;
import io.systom.coin.service.TaskService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

import static io.systom.coin.service.GoodsService.DATE_FORMAT;

/*
 * create joonwoo 2018. 7. 3.
 * 
 */
@RestController
@RequestMapping("/v1/goods")
public class GoodsController extends AbstractController{
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(GoodsController.class);
    private static org.slf4j.Logger goodsErrorLogger = LoggerFactory.getLogger("goodsError");

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private TaskService taskService;

    public enum GOODS_TYPE { wait, running, close }

    @PostMapping
    public ResponseEntity<?> registerGoods(@RequestAttribute String userId,
                                           @RequestBody Goods target) {
        try {
            target.setAuthorId(userId);
            Goods registerGoods = goodsService.registerGoods(target);
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
            List<Goods> registerGoodsList = new ArrayList<>();
            String nowTime = new SimpleDateFormat(DATE_FORMAT).format(new Date());
            Goods searchGoods = null;
            if (type != null && identityService.isManager(userId)) {
                List<String> typeList = Arrays.asList(type.split(","));

                if (typeList.contains(GOODS_TYPE.wait.name())) {
                    logger.debug("retrieveGoodsList wait");
                    searchGoods = new Goods();
                    searchGoods.setAuthorId(userId);
                    searchGoods.setExchange(exchange);
                    searchGoods.setInvestStart(nowTime);
                    searchGoods.setDisplay(false);
                    registerGoodsList.addAll(goodsService.retrieveGoodsList(searchGoods));
                }
                if (typeList.contains(GOODS_TYPE.running.name())) {
                    logger.debug("retrieveGoodsList running");
                    searchGoods = new Goods();
                    searchGoods.setAuthorId(userId);
                    searchGoods.setExchange(exchange);
                    searchGoods.setInvestStart(nowTime);
                    searchGoods.setInvestEnd(nowTime);
                    searchGoods.setDisplay(false);
                    registerGoodsList.addAll(goodsService.retrieveGoodsList(searchGoods));
                }
                if (typeList.contains(GOODS_TYPE.close.name())) {
                    logger.debug("retrieveGoodsList close");
                    searchGoods = new Goods();
                    searchGoods.setAuthorId(userId);
                    searchGoods.setExchange(exchange);
                    searchGoods.setInvestEnd(nowTime);
                    searchGoods.setDisplay(false);
                    registerGoodsList.addAll(goodsService.retrieveGoodsList(searchGoods));
                }
            } else {
                searchGoods = new Goods();
                searchGoods.setCollectStart(nowTime);
                searchGoods.setCollectEnd(nowTime);
                searchGoods.setDisplay(true);
                searchGoods.setExchange(exchange);
                searchGoods.setAuthorId(userId);
                registerGoodsList = goodsService.retrieveGoodsList(searchGoods);
            }
            return success(registerGoodsList);
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
            Goods registerGoods = goodsService.getGoods(id, userId);
            int nowTime = Integer.parseInt(new SimpleDateFormat(DATE_FORMAT).format(new Date()));

            if (!identityService.isManager(userId)
                    && Integer.parseInt(registerGoods.getCollectStart()) > nowTime
                    && Integer.parseInt(registerGoods.getCollectEnd()) < nowTime) {
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
    public ResponseEntity<?> updateGoodsHide(@RequestAttribute String userId,
                                             @PathVariable Integer id) {
        try {
            Goods registerGoods = goodsService.updateGoodsHide(id, userId);
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
    public ResponseEntity<?> updateGoodsShow(@RequestAttribute String userId,
                                             @PathVariable Integer id) {
        try {
            Goods registerGoods = goodsService.updateGoodsShow(id, userId);
            return success(registerGoods);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Throwable: ", t);
            return new OperationException().response();
        }
    }

    @PostMapping("/{id}/error")
    public ResponseEntity<?> taskError(@PathVariable Integer id,
                                       @RequestBody String body) {
        try {
            goodsErrorLogger.error("GoodsID[{}] {}",id, body);
            return success();
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Throwable: ", t);
            return new OperationException().response();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateGoods(@PathVariable Integer id,
                                         @RequestBody Goods target,
                                         @RequestAttribute String userId) {
        try {
            target.setId(id);
            target.setAuthorId(userId);
            Goods registerGoods = goodsService.updateGoods(target);
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
    public ResponseEntity<?> deleteGoods(@RequestAttribute String userId,
                                         @PathVariable Integer id) {
        try {
            Goods deleteCollectGoods = goodsService.deleteGoods(id, userId);
            return success(deleteCollectGoods);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Throwable: ", t);
            return new OperationException().response();
        }
    }


    @PostMapping("/{id}/backTest")
    public ResponseEntity<?> createTestResult(@PathVariable Integer id,
                                              @RequestAttribute String userId,
                                              @RequestBody Task task) {
        try {
            task.setGoodsId(id);
            task.setUserId(userId);
            task.setSessionType(Task.SESSION_TYPES.backtest.name());
            Goods registerGoods = taskService.createGoodsBackTest(task);
            return success(registerGoods);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Throwable: ", t);
            return new OperationException().response();
        }
    }
}