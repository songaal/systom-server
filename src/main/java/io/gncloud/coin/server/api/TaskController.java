package io.gncloud.coin.server.api;

import io.gncloud.coin.server.exception.AbstractException;
import io.gncloud.coin.server.message.RunBackTestRequest;
import io.gncloud.coin.server.message.RunLiveAgentRequest;
import io.gncloud.coin.server.model.Task;
import io.gncloud.coin.server.service.StrategyService;
import io.gncloud.coin.server.service.TaskService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * create joonwoo 2018. 3. 21.
 *
 */
@RestController
@RequestMapping(value = "/v1/tasks", produces = "application/json")
public class TaskController extends AbstractController {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(TaskController.class);
    @Autowired
    private TaskService taskService;

    @Autowired
    private StrategyService strategyService;

    @PostMapping("/test")
    public ResponseEntity<?> runBackTestTask(@RequestHeader(name = "X-coincloud-user-id") String token, @RequestBody RunBackTestRequest runBackTestRequest) {
        try {
            Task task = runBackTestRequest.getTask();
            logger.debug("Run Task: {}", runBackTestRequest.getTask());
            if(task != null) {
                task = taskService.runBackTestTask(token, task);
                String ecsTask = task.getEcsTaskId();
                return success(task);
            }

        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        }
        return null;
    }

    @PostMapping("/agent")
    public ResponseEntity<?> runLiveAgentTask(@RequestHeader(name = "X-coincloud-user-id") String token, @RequestBody RunLiveAgentRequest runLiveAgentRequest) {
        try {
            Task task = null;
            if (runLiveAgentRequest.getAgentId() != null) {
                //TOOD token으로 부터 user 를 얻는다.
                String user = null;
                String exchangeList = null;
                task = taskService.runLiveAgentTask(user, runLiveAgentRequest.getAgentId(), exchangeList, runLiveAgentRequest.getUserPin());
                task.setStartTime("");
                task.setEndTime("");
                task.setDataFrequency("");
                return success(task);
            }
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<?> getBackTestHistory(@RequestHeader(name = "X-coincloud-user-id") String token, @RequestParam String strategyId){
        try {
            List<Task> taskHistory = taskService.getTestHistory(token, strategyId);
            return new ResponseEntity<>(taskHistory, HttpStatus.OK);
        } catch (AbstractException e){
            logger.error("", e);
            return e.response();
        }
    }

}