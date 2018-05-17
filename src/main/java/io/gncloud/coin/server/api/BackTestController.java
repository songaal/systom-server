package io.gncloud.coin.server.api;

import io.gncloud.coin.server.exception.AbstractException;
import io.gncloud.coin.server.message.RunBackTestRequest;
import io.gncloud.coin.server.model.Task;
import io.gncloud.coin.server.service.TaskService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/backtest")
    public ResponseEntity<?> runBackTestTask(@CookieValue(value = ACCESS_TOKEN) String accessToken, @RequestAttribute String userId, @RequestBody RunBackTestRequest runBackTestRequest) {
        try {
            Task task = runBackTestRequest.getTask();
            logger.debug("Run Task: {}", runBackTestRequest.getTask());
            if(task != null) {
                task = taskService.runBackTestTask(userId, accessToken, task);
                return success(task);
            }

        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        }
        return null;
    }

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

    @PostMapping("/waitRunBackTestTask")
    public ResponseEntity<?> waitRunBackTestTask(@RequestBody String timeout) throws Exception {
        return new ResponseEntity<>(taskService.waitRunBackTestTask(timeout), HttpStatus.OK);
    }

    @PostMapping("/results/{id}")
    public ResponseEntity<?> backtestResult(@PathVariable String id, @RequestBody String resultJson) throws Exception {
        taskService.registerBacktestResult(id, resultJson);
        return new ResponseEntity<>(resultJson, HttpStatus.OK);
    }

}