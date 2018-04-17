package io.gncloud.coin.server.api;

import io.gncloud.coin.server.exception.AbstractException;
import io.gncloud.coin.server.exception.OperationException;
import io.gncloud.coin.server.exception.ParameterException;
import io.gncloud.coin.server.message.AgentRequestParams;
import io.gncloud.coin.server.model.Agent;
import io.gncloud.coin.server.model.Order;
import io.gncloud.coin.server.model.Task;
import io.gncloud.coin.server.service.AgentService;
import io.gncloud.coin.server.service.OrderService;
import io.gncloud.coin.server.service.TaskService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.gncloud.coin.server.api.IdentityController.ACCESS_TOKEN;

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

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> insertAgent(@RequestAttribute String userId, @RequestBody Agent agent) {
        try {
            agent.setUserId(userId);
            Agent registerAgent = agentService.insertAgent(agent);
            return success(registerAgent);
        } catch (AbstractException e){
            logger.error("", e);
            return e.response();
        }
    }

    @GetMapping
    public ResponseEntity<?> selectAgent(@RequestAttribute String userId) {
        try {
            List<Agent> agentList = agentService.selectAgent(userId);
            return success(agentList);
        } catch (AbstractException e){
            logger.error("", e);
            return e.response();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAgent(@PathVariable Integer id) {
        try {
            Agent agent = agentService.getAgent(id);
            return success(agent);
        } catch (AbstractException e){
            logger.error("", e);
            return e.response();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAgent(@RequestAttribute String userId, @PathVariable Integer id) {
        try {
            Agent agent = new Agent();
            agent.setUserId(userId);
            agent.setId(id);
            agent = agentService.deleteAgent(agent);
            return success(agent);
        } catch (AbstractException e){
            logger.error("", e);
            return e.response();
        }
    }

    @PostMapping("/{agentId}/actions")
    public ResponseEntity<?> runLiveAgentTask(@CookieValue(value = ACCESS_TOKEN) String accessToken, @PathVariable Integer agentId, @RequestAttribute String userId, @RequestBody AgentRequestParams agentRequestParams) throws ParameterException, OperationException {
        Task task = null;
        try {
            if(AgentRequestParams.RUN_ACTION.equalsIgnoreCase(agentRequestParams.getAction())) {
                agentService.updateAgentMode(agentId, agentRequestParams.getMode());
                boolean isLiveMode = AgentRequestParams.LIVE_MODE.equalsIgnoreCase(agentRequestParams.getMode());
                task = taskService.runAgentTask(userId, accessToken, agentId, isLiveMode);
                task.setStartTime("");
                task.setEndTime("");
                task.setDataFrequency("");
            } else if(AgentRequestParams.STOP_ACTION.equalsIgnoreCase(agentRequestParams.getAction())) {
                task = taskService.stopAgentTask(userId, agentId);
            }
            return success(task);
        } catch (Throwable e) {
            logger.error("", e);
            logger.debug("task = {}", task);
            throw e;
        }
    }

    @GetMapping("/{agentId}/trade")
    public ResponseEntity<?> tradeHistory(@PathVariable Integer agentId) throws OperationException {
        try {
            List<Order> orderHistories = orderService.selectAgentHistory(agentId);
            return success(orderHistories);
        } catch(Throwable t) {
            logger.error("", t);
            throw t;
        }
    }

}