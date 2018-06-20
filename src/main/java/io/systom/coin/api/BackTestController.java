//package io.systom.coin.api;
//
//
//import io.systom.coin.exception.OperationException;
//import io.systom.coin.service.TaskService;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
///*
// * create joonwoo 2018. 3. 21.
// *
// */
//@RestController
//@RequestMapping(value = "/v1/tasks", produces = "application/json")
//public class BackTestController extends AbstractController {
//
//    private static org.slf4j.Logger logger = LoggerFactory.getLogger(BackTestController.class);
//
//    @Autowired
//    private TaskService taskService;
//
//
//
////    @PostMapping("/backtest")
////    public ResponseEntity<?> waitRunBackTestTask(@CookieValue(ACCESS_TOKEN) String accessToken, @RequestAttribute String userId, @RequestBody Task task) throws TimeoutException, InterruptedException {
////        try {
////            task.setUserId(userId);
////            task.setAccessToken(accessToken);
////            Map<String, Object> resultJson = taskService.runAndWaitBackTestTask(task);
////            return new ResponseEntity<>(resultJson, HttpStatus.OK);
////        } catch (AbstractException e){
////            logger.error("", e);
////            return e.response();
////        } catch (Throwable t) {
////            logger.error("", t);
////            return new OperationException(t.getMessage()).response();
////        }
////    }
//
//    @GetMapping("/{taskId}/model")
//    public ResponseEntity<?> getStrategyModel(@RequestAttribute String userId,
//                                              @PathVariable Integer taskId,
//                                              @RequestParam(required = false) Integer version) {
//        try {
////            OldStrategy registerOldStrategy = taskService.getBackTestModel(taskId, userId, version);
////            Map<String, String> response = new HashMap<>();
////            response.put("code", registerOldStrategy.getCode());
////            response.put("options", registerOldStrategy.getOptions());
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (Throwable t) {
//            logger.error("", t);
//            return new OperationException(t.getMessage()).response();
//        }
//    }
//
//    @PostMapping("/{id}/result")
//    public ResponseEntity<?> backtestResult(@PathVariable Integer id, @RequestBody Map<String, Object> resultJson) throws Exception {
//        logger.debug("[BACKTEST RESULT] id: {}", id);
//        taskService.registerBacktestResult(id, resultJson);
//        return new ResponseEntity<>(resultJson, HttpStatus.OK);
//    }
//
//    @GetMapping("/getBacktestResult")
//    public ResponseEntity<?> getBackTestResult(){
//        return success(taskService.getBackTestResult());
//    }
//
////    @GetMapping
////    public ResponseEntity<?> getBackTestHistory(@RequestParam String strategyId){
////        try {
////            List<Task> taskHistory = taskService.getBackTestHistory(strategyId);
////            return new ResponseEntity<>(taskHistory, HttpStatus.OK);
////        } catch (AbstractException e){
////            logger.error("", e);
////            return e.response();
////        }
////    }
//
//}