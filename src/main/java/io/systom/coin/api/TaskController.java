package io.systom.coin.api;

import io.systom.coin.exception.AbstractException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.model.Strategy;
import io.systom.coin.model.Task;
import io.systom.coin.service.TaskService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static io.systom.coin.api.IdentityController.ACCESS_TOKEN;

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

    @PostMapping("/backTest")
    public ResponseEntity<?> runBackTest(@CookieValue(ACCESS_TOKEN) String accessToken,
                                         @RequestAttribute String userId,
                                         @RequestBody Task task) throws TimeoutException, InterruptedException {
        try {
            task.setUserId(userId);
            task.setAccessToken(accessToken);
            Map<String, Object> resultJson = taskService.runAndWaitBackTestTask(task);
            return new ResponseEntity<>(resultJson, HttpStatus.OK);
        } catch (AbstractException e){
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("", t);
            return new OperationException(t.getMessage()).response();
        }
    }

    @GetMapping("/{taskId}/model")
    public ResponseEntity<?> getStrategyModel(@RequestAttribute String userId,
                                              @PathVariable Integer taskId,
                                              @RequestParam(required = false) Integer version) {
        try {
            Strategy registerStrategy = taskService.getBackTestModel(taskId, userId, version);
            Map<String, String> response = new HashMap<>();
            response.put("code", registerStrategy.getCode());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Throwable t) {
            logger.error("", t);
            return new OperationException(t.getMessage()).response();
        }
    }

    @PostMapping("/{id}/result")
    public ResponseEntity<?> backtestResult(@PathVariable Integer id, @RequestBody Map<String, Object> resultJson) throws Exception {
        logger.debug("[BACKTEST RESULT] id: {}", id);
        taskService.registerBacktestResult(id, resultJson);
        return new ResponseEntity<>(resultJson, HttpStatus.OK);
    }

    @GetMapping("/getBacktestResult")
    public ResponseEntity<?> getBackTestResult(){
        return success(taskService.getBackTestResult());
    }

}