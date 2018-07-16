//package io.gncloud.coin.server.api;
//
//import com.google.gson.Gson;
//import AbstractException;
//import OperationException;
//import ParameterException;
//import io.gncloud.coin.server.message.AgentRequestParams;
//import io.gncloud.coin.server.model.Agent;
//import io.gncloud.coin.server.model.Order;
//import io.gncloud.coin.server.model.Task;
//import io.gncloud.coin.server.service.AgentService;
//import io.gncloud.coin.server.service.OrderService;
//import io.gncloud.coin.server.service.TaskService;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static IdentityController.ACCESS_TOKEN;
//
///*
// * create joonwoo 2018. 4. 13.
// *
// */
//@RestController
//@RequestMapping(value = "/v1/agents", produces = "application/json")
//public class AgentController extends AbstractController {
//
//    @Autowired
//    private TaskService taskService;
//
//    private static org.slf4j.Logger logger = LoggerFactory.getLogger(AgentController.class);
//
//    @Autowired
//    private AgentService agentService;
//
//    @Autowired
//    private OrderService orderService;
//
//    private Gson gson = new Gson();
//
//    @PostMapping
//    public ResponseEntity<?> insertAgent(@RequestAttribute String userId, @RequestBody Agent agent) {
//        try {
//            agent.setUserId(userId);
//            Agent registerAgent = agentService.insertAgent(agent);
//            return success(registerAgent);
//        } catch (AbstractException e){
//            logger.error("", e);
//            return e.response();
//        }
//    }
//
//    @GetMapping
//    public ResponseEntity<?> selectAgent(@RequestAttribute String userId) {
//        try {
//            List<Agent> agentList = agentService.selectAgent(userId);
//            return success(agentList);
//        } catch (AbstractException e){
//            logger.error("", e);
//            return e.response();
//        }
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getAgent(@PathVariable Integer id) {
//        try {
//            Agent agent = agentService.getAgent(id);
//            return success(agent);
//        } catch (AbstractException e){
//            logger.error("", e);
//            return e.response();
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteAgent(@RequestAttribute String userId, @PathVariable Integer id) {
//        try {
//            Agent agent = new Agent();
//            agent.setUserId(userId);
//            agent.setId(id);
//            agent = agentService.deleteAgent(agent);
//            return success(agent);
//        } catch (AbstractException e){
//            logger.error("", e);
//            return e.response();
//        }
//    }
//
//    @PostMapping("/{agentId}/actions")
//    public ResponseEntity<?> runLiveAgentTask(@CookieValue(value = ACCESS_TOKEN) String accessToken, @PathVariable Integer agentId, @RequestAttribute String userId, @RequestBody AgentRequestParams agentRequestParams) throws ParameterException, OperationException {
//        Task task = null;
//        try {
//            if(AgentRequestParams.RUN_ACTION.equalsIgnoreCase(agentRequestParams.getAction())) {
//                agentService.updateAgentMode(agentId, agentRequestParams.getMode());
//                boolean isLiveMode = AgentRequestParams.LIVE_MODE.equalsIgnoreCase(agentRequestParams.getMode());
//                task = taskService.runAgentTask(userId, accessToken, agentId, isLiveMode);
//                task.setStartDate("");
//                task.setEndDate("");
//                task.setTimeInterval("");
//            } else if(AgentRequestParams.STOP_ACTION.equalsIgnoreCase(agentRequestParams.getAction())) {
//                task = taskService.stopAgentTask(userId, agentId);
//            }
//            return success(task);
//        } catch (Throwable e) {
//            logger.error("", e);
//            logger.debug("task = {}", task);
//            throw e;
//        }
//    }
//
//    @GetMapping("/{agentId}/trade")
//    public ResponseEntity<?> tradeHistory(@PathVariable Integer agentId) throws OperationException {
//        try {
//            List<Order> orderHistories = orderService.selectOrderHistory(agentId);
//            return success(orderHistories);
//        } catch(Throwable t) {
//            logger.error("", t);
//            throw t;
//        }
//    }
//
//    @GetMapping("/{agentId}/model")
//    public ResponseEntity<?> getStrategyModel(@PathVariable Integer agentId) {
//        try {
//            Agent registerAgent = agentService.getAgent(agentId);
//            Map<String, String> response = new HashMap<>();
//            response.put("code", registerAgent.getCode());
//            Map<String, Object> optionMap = new HashMap<>();
//            if (registerAgent.getOptions() != null && !"".equals(registerAgent.getOptions())) {
//                List<Map<String, Object>> optionList = gson.fromJson(registerAgent.getOptions(), List.class);
//                int optionSize = optionList.size();
//                for (int i = 0; i < optionSize; i++) {
//                    String key = String.valueOf(optionList.get(i).get("key"));
//                    Object value = optionList.get(i).get("value");
//                    optionMap.put(key, value);
//                }
//            }
//            response.put("options", gson.toJson(optionMap));
//            return new ResponseEntity<>(response, HttpStatus.OK);
//        } catch (AbstractException e) {
//            logger.error("", e);
//            return e.response();
//        }
//    }
//}