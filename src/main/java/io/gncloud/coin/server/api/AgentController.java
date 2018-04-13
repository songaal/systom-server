package io.gncloud.coin.server.api;

import io.gncloud.coin.server.exception.AbstractException;
import io.gncloud.coin.server.message.AgentRequestParams;
import io.gncloud.coin.server.model.Task;
import io.gncloud.coin.server.model.Agent;
import io.gncloud.coin.server.service.AgentService;
import io.gncloud.coin.server.service.TaskService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 * create joonwoo 2018. 4. 13.
 * 
 */
@RestController
@RequestMapping(value = "/v1/agents", produces = "application/json")
public class AgentController extends AbstractController {

    @Autowired
    private TaskService taskService;


    private static org.slf4j.Logger logger = LoggerFactory.getLogger(AgentController.class);

    @Autowired
    private AgentService agentService;

    @PostMapping
    public ResponseEntity<?> insertAgent(@RequestBody Agent agent) {
        try {
            Agent registerAgent = agentService.insertAgent(agent);

            return success(registerAgent);
        } catch (AbstractException e){
            logger.error("", e);
            return e.response();
        }
    }

    @GetMapping
    public void selectAgent() {

    }

    @GetMapping("/{id}")
    public void getAgent() {

    }

    @PutMapping
    public void updateAgent() {

    }

    @DeleteMapping
    public void deleteAgent() {

    }

    @PostMapping("/{agentId}/actions")
    public ResponseEntity<?> runLiveAgentTask(@RequestAttribute String userId, @RequestBody AgentRequestParams agentRequestParams) {
        Task task = null;
        try {
            if(AgentRequestParams.RUN_ACTION.equalsIgnoreCase(agentRequestParams.getAction())) {
                boolean isLiveMode = AgentRequestParams.LIVE_MODE.equalsIgnoreCase(agentRequestParams.getMode());
                task = taskService.runAgentTask(userId, agentRequestParams.getAgentId(), agentRequestParams.getExchangeKeyId(), isLiveMode);
                task.setStartTime("");
                task.setEndTime("");
                task.setDataFrequency("");
                return success(task);

            } else if(AgentRequestParams.STOP_ACTION.equalsIgnoreCase(agentRequestParams.getAction())) {
                task = taskService.stopAgentTask(userId, agentRequestParams.getAgentId());
                return success(task);
            }
        } catch (AbstractException e) {
            logger.error("", e);
            logger.debug("task = {}", task);
            return e.response();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}