package io.systom.coin.api;

import com.google.gson.Gson;
import io.systom.coin.exception.AbstractException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.model.TraderTask;
import io.systom.coin.model.TraderTaskResult;
import io.systom.coin.service.TaskService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 * create joonwoo 2018. 6. 20.
 *
 */
@RestController
@RequestMapping(value = "/v1/tasks")
public class TaskController extends AbstractController {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<?> taskRun(@RequestAttribute String userId,
                                     @RequestBody TraderTask traderTask) throws InterruptedException {
        try {
            traderTask.setUserId(userId);
            traderTask.setSessionType("backtest");
            TraderTaskResult traderTaskResult = taskService.syncBackTest(traderTask);
            return success(traderTaskResult);
        } catch (AbstractException e){
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("", t);
            return new OperationException().response();
        }
    }

    @GetMapping("/{taskId}/model")
    public ResponseEntity<?> syncBackTest(@PathVariable String taskId) throws InterruptedException {
        try {
            String code = taskService.getTaskModel(taskId);
            return success(code);
        } catch (AbstractException e){
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("", t);
            return new OperationException(t.getMessage()).response();
        }
    }

    @PostMapping("/{taskId}/result")
    public ResponseEntity<?> testTaskResult(@PathVariable String taskId,
                                            @RequestBody String taskResultJson) throws Exception {
        logger.debug("[BACK TEST RESULT] taskId: {}, response: {}", taskId, taskResultJson);
        TraderTaskResult traderTaskResult = new Gson().fromJson(taskResultJson, TraderTaskResult.class);
        TraderTaskResult saveResult = taskService.registerBackTestResult(taskId, traderTaskResult);
        return success(saveResult);
    }

}