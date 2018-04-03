package io.gncloud.coin.server.api;

import io.gncloud.coin.server.exception.AbstractException;
import io.gncloud.coin.server.model.RequestTask;
import io.gncloud.coin.server.model.Task;
import io.gncloud.coin.server.service.StrategyService;
import io.gncloud.coin.server.service.TasksService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            if(requestTask.getTask() != null && requestTask.getTask().isLive()){
                task = taskService.liveMode(token, requestTask);
                task.setStart("");
                task.setEnd("");
                task.setDataFrequency("");
            }else{
                task = taskService.backTestMode(token, requestTask);
            }
            return success(task);
        } catch (AbstractException e){
            logger.error("", e);
            return e.response();
        }
    }
}