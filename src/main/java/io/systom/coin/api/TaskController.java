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

    @PostMapping
    public ResponseEntity<?> runBackTest(@CookieValue(ACCESS_TOKEN) String accessToken,
                                         @RequestAttribute String userId,
                                         @RequestBody Task task) throws TimeoutException, InterruptedException {
        try {
            String sessionType = task.getSessionType();
            if (Task.SESSION_TYPES.live.equals(sessionType) || Task.SESSION_TYPES.paper.equals(sessionType)) {
//                TODO 라이브, 페이퍼 모드

                return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
            } else if (Task.SESSION_TYPES.backtest.equals(sessionType)) {
                task.setUserId(userId);
                task.setAccessToken(accessToken);
                Map<String, Object> resultJson = taskService.syncBackTest(task);
                return new ResponseEntity<>(resultJson, HttpStatus.OK);
            } else {
                String message = "Unknown session type";
                return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
            }
        } catch (AbstractException e){
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("", t);
            return new OperationException().response();
        }
    }

    @GetMapping("/{taskId}/model")
    public ResponseEntity<?> getStrategyModel(@RequestAttribute String userId,
                                              @PathVariable String taskId,
                                              @RequestParam(required = false) Integer version) {
        try {
            Strategy registerStrategy = taskService.getBackTestModel(taskId, userId, version);
            Map<String, String> response = new HashMap<>();
            response.put("code", registerStrategy.getCode());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Throwable t) {
            logger.error("", t);
            return new OperationException().response();
        }
    }

    @PostMapping("/{id}/result")
    public ResponseEntity<?> taskResult(@PathVariable String id,
                                        @RequestBody Map<String, Object> resultJson) throws Exception {
        logger.debug("[BACK TEST RESULT] id: {}", id);
        taskService.registerBackTestResult(id, resultJson);
        return new ResponseEntity<>(resultJson, HttpStatus.OK);
    }


//    test api
    @GetMapping("/getBacktestResult")
    public ResponseEntity<?> getBackTestResult(){
        return success(taskService.getBackTestResult());
    }

}