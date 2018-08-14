package io.systom.coin.api;

import com.amazonaws.services.ecs.model.Task;
import io.systom.coin.exception.AbstractException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.exception.ParameterException;
import io.systom.coin.model.Goods;
import io.systom.coin.model.TraderTask;
import io.systom.coin.service.GoodsService;
import io.systom.coin.service.IdentityService;
import io.systom.coin.service.TaskService;
import io.systom.coin.utils.EcsUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static io.systom.coin.service.GoodsService.DATE_FORMAT;
import static io.systom.coin.service.GoodsService.TIME_FORMAT;

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
    @Autowired
    private EcsUtils ecsUtils;

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
            String nowTime = new SimpleDateFormat(DATE_FORMAT + TIME_FORMAT).format(new Date());
            Goods searchGoods = null;
            if (type != null && identityService.isManager(userId)) {
                List<String> typeList = Arrays.asList(type.split(","));

                if (typeList.contains(GOODS_TYPE.wait.name())) {
                    logger.debug("retrieveGoodsList wait");
                    searchGoods = new Goods();
                    searchGoods.setUserId(userId);
                    searchGoods.setExchange(exchange);
                    searchGoods.setInvestStart(nowTime);
                    searchGoods.setDisplay(false);
                    registerGoodsList.addAll(goodsService.retrieveGoodsList(searchGoods));
                }
                if (typeList.contains(GOODS_TYPE.running.name())) {
                    logger.debug("retrieveGoodsList running");
                    searchGoods = new Goods();
                    searchGoods.setUserId(userId);
                    searchGoods.setExchange(exchange);
                    searchGoods.setInvestStart(nowTime);
                    searchGoods.setInvestEnd(nowTime);
                    searchGoods.setDisplay(false);
                    registerGoodsList.addAll(goodsService.retrieveGoodsList(searchGoods));
                }
                if (typeList.contains(GOODS_TYPE.close.name())) {
                    logger.debug("retrieveGoodsList close");
                    searchGoods = new Goods();
                    searchGoods.setUserId(userId);
                    searchGoods.setExchange(exchange);
                    searchGoods.setInvestEnd(nowTime);
                    searchGoods.setDisplay(false);
                    registerGoodsList.addAll(goodsService.retrieveGoodsList(searchGoods));
                }
                int goodsSize = registerGoodsList.size();
                List<String> runTaskList = ecsUtils.getRunningTaskList();
                for (int i=0; i < goodsSize; i++) {
                    Goods tmpGoods = registerGoodsList.get(i);
                    String goodsTaskArn = tmpGoods.getTaskEcsId();
                    if (goodsTaskArn != null) {
                        int taskSize = runTaskList.size();
                        for (int j=0; j < taskSize; j++) {
                            String arn = runTaskList.get(j);
                            if (goodsTaskArn.equals(arn)) {
                                tmpGoods.setTaskRunning(true);
                                break;
                            }
                        }
                    }
                }
            } else {
                searchGoods = new Goods();
                searchGoods.setCollectStart(nowTime);
                searchGoods.setCollectEnd(nowTime);
                searchGoods.setDisplay(true);
                searchGoods.setExchange(exchange);
                searchGoods.setUserId(userId);
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
//            int nowTime = Integer.parseInt(new SimpleDateFormat(DATE_FORMAT).format(new Date()));
//            if (!identityService.isManager(userId)
//                    && Integer.parseInt(registerGoods.getCollectStart()) > nowTime
//                    && Integer.parseInt(registerGoods.getCollectEnd()) < nowTime) {
//                throw new AuthenticationException();
//            }
            registerGoods.setTaskRunning(isEcsTaskRunning(registerGoods.getTaskEcsId()));
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


    @PostMapping("/{id}/actions")
    public ResponseEntity<?> goodsActions(@PathVariable Integer id,
                                              @RequestAttribute String userId,
                                              @RequestBody TraderTask traderTask) {
        try {
            traderTask.setGoodsId(id);
            traderTask.setUserId(userId);
            if (TraderTask.ACTIONS.backtest.name().equalsIgnoreCase(traderTask.getAction())) {
                traderTask.setSessionType("backtest");
                Goods registerGoods = taskService.createGoodsBackTest(traderTask);
                registerGoods.setTaskRunning(isEcsTaskRunning(registerGoods.getTaskEcsId()));
                return success(registerGoods);
            } else if (TraderTask.ACTIONS.start.name().equals(traderTask.getAction())) {
                traderTask.setSessionType("live");
                traderTask.setInitCash(10000);
                Task task = taskService.liveTaskRun(traderTask);
                return success(task);
            } else if (TraderTask.ACTIONS.stop.name().equals(traderTask.getAction())) {
                traderTask.setSessionType("live");
                traderTask.setInitCash(10000);
                Task task = taskService.liveTaskStop(traderTask);
                return success(task);
            } else if (TraderTask.ACTIONS.reset.name().equals(traderTask.getAction())) {
                Goods registerGoods = goodsService.resetGoodsTestResult(id);
                registerGoods.setTaskRunning(isEcsTaskRunning(registerGoods.getTaskEcsId()));
                return success(registerGoods);
            } else {
                throw new ParameterException("action");
            }
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Throwable: ", t);
            return new OperationException().response();
        }
    }

    private boolean isEcsTaskRunning(String taskEcsId) {
        if (taskEcsId == null) {
            return false;
        }

        Task task = ecsUtils.getDescribeTasks(taskEcsId);
        return "RUNNING".equalsIgnoreCase(task.getLastStatus());
    }





//    TODO 개발중
    public ResponseEntity<?> tmpGoodsActions(@PathVariable Integer id,
                                          @RequestAttribute String userId,
                                          @RequestBody TraderTask traderTask) {
        try {
            traderTask.setGoodsId(id);
            traderTask.setUserId(userId);
            if (TraderTask.ACTIONS.show.name().equals(traderTask.getAction())) {
                Goods registerGoods = goodsService.updateGoodsHide(id, userId);
                return success(registerGoods);
            } else if (TraderTask.ACTIONS.hide.name().equals(traderTask.getAction())) {
                Goods registerGoods = goodsService.updateGoodsShow(id, userId);
                return success(registerGoods);
            } else {
                throw new ParameterException("action");
            }
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Throwable: ", t);
            return new OperationException().response();
        }
    }

}