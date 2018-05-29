package io.gncloud.coin.server.api;

import io.gncloud.coin.server.exception.AbstractException;
import io.gncloud.coin.server.exception.OperationException;
import io.gncloud.coin.server.model.Task;
import io.gncloud.coin.server.service.TaskService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Value("${backtest.base}")
    private String base;

    @Value("${backtest.capicalBase}")
    private float capicalBase;

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
            task.setBase(base);
            task.setCapitalBase(capicalBase);
            task.setUserId(userId);
            task.setAccessToken(accessToken);

            Map<String, Object> resultJson = taskService.waitRunBackTestTask(task);
            return new ResponseEntity<>(resultJson, HttpStatus.OK);
        } catch (AbstractException e){
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("", t);
            return new OperationException(t.getMessage()).response();
        }
    }

    @PostMapping("/{id}/result")
    public ResponseEntity<?> backtestResult(@PathVariable String id, @RequestBody Map<String, Object> resultJson) throws Exception {
        taskService.registerBacktestResult(id, resultJson);
        return new ResponseEntity<>(resultJson, HttpStatus.OK);
    }

}