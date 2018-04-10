package io.gncloud.coin.server.api;

import io.gncloud.coin.server.exception.AbstractException;
import io.gncloud.coin.server.model.RequestTask;
import io.gncloud.coin.server.model.Task;
import io.gncloud.coin.server.service.StrategyService;
import io.gncloud.coin.server.service.TasksService;
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
public class TasksController extends AbstractController{

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(TasksController.class);
    @Autowired
    private TasksService taskService;

    @Autowired
    private StrategyService strategyService;

    @PostMapping
    public ResponseEntity<?> createTask(@RequestHeader(name = "X-coincloud-user-id") String token, @RequestBody RequestTask requestTask){
        try{
            Task task = null;
            if(requestTask.getTask() != null && false){
                task = taskService.liveMode(token, requestTask);
                task.setStartTime("");
                task.setEndTime("");
                task.setDataFrequency("");
            }else{
                logger.debug("task: {}", requestTask.getTask());
                task = taskService.backTestMode(token, requestTask);
            }
            return success(task);
        } catch (AbstractException e){
            logger.error("", e);
            return e.response();
        }
    }

    @GetMapping
    public ResponseEntity<?> getBackTestHistory(@RequestHeader(name = "X-coincloud-user-id") String token, @RequestParam String strategysId){
        try {
            List<Task> taskHistory = taskService.getBackTestHistory(token, strategysId);
            return new ResponseEntity<>(taskHistory, HttpStatus.OK);
        } catch (AbstractException e){
            logger.error("", e);
            return e.response();
        }
    }
}