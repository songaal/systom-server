package io.gncloud.coin.server.api;

import io.gncloud.coin.server.exception.AbstractException;
import io.gncloud.coin.server.exception.OperationException;
import io.gncloud.coin.server.model.Strategy;
import io.gncloud.coin.server.model.Task;
import io.gncloud.coin.server.service.TaskService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static io.gncloud.coin.server.api.IdentityController.ACCESS_TOKEN;

/*
 * create joonwoo 2018. 3. 21.
 *
 */
@RestController
@RequestMapping(value = "/v1/tasks", produces = "application/json")
public class BackTestController extends AbstractController {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(BackTestController.class);

    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<?> getBackTestHistory(@RequestParam String strategyId){
        try {
            List<Task> taskHistory = taskService.getBackTestHistory(strategyId);
            return new ResponseEntity<>(taskHistory, HttpStatus.OK);
        } catch (AbstractException e){
            logger.error("", e);
            return e.response();
        }
    }

    @PostMapping("/backtest")
    public ResponseEntity<?> waitRunBackTestTask(@CookieValue(ACCESS_TOKEN) String accessToken, @RequestAttribute("userId") String userId, @RequestBody Task task) throws TimeoutException, InterruptedException {
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
            response.put("options", registerStrategy.getOptions());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("", t);
            return new OperationException(t.getMessage()).response();
        }
    }

    @PostMapping("/{id}/result")
    public ResponseEntity<?> backtestResult(@PathVariable Integer id, @RequestBody Map<String, Object> resultJson) throws Exception {
        logger.debug("[BACKTEST RESULT] id: {}, result: {}", id, resultJson);
        taskService.registerBacktestResult(id, resultJson);
        return new ResponseEntity<>(resultJson, HttpStatus.OK);
    }

    @GetMapping("/getBacktestResult")
    public ResponseEntity<?> getBackTestResult(){
        return success(taskService.getBackTestResult());
    }
}