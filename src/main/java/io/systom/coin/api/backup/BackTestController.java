//package io.systom.coin.api.backup;
//
//
//import io.systom.coin.api.AbstractController;
//import io.systom.coin.exception.AbstractException;
//import io.systom.coin.exception.OperationException;
//import io.systom.coin.model.TraderTask;
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
//    @PostMapping("/backtest")
//    public ResponseEntity<?> waitRunBackTestTask(@RequestAttribute String userId,
//                                                 @RequestBody TraderTask testTask) throws InterruptedException {
//        try {
//            testTask.setUserId(userId);
//            Map<String, Object> resultJson = taskService.syncBackTest(testTask);
//            return new ResponseEntity<>(resultJson, HttpStatus.OK);
//        } catch (AbstractException e){
//            logger.error("", e);
//            return e.response();
//        } catch (Throwable t) {
//            logger.error("", t);
//            return new OperationException(t.getMessage()).response();
//        }
//    }
//
////    @GetMapping("/{taskId}/model")
////    public ResponseEntity<?> getStrategyModel(@RequestAttribute String userId,
////                                              @PathVariable Integer taskId,
////                                              @RequestParam(required = false) Integer version) {
////        try {
////            Strategy registerOldStrategy = taskService.getBackTestModel(taskId, userId, version);
////            Map<String, String> response = new HashMap<>();
////            response.put("code", registerOldStrategy.getCode());
////            return new ResponseEntity<>(HttpStatus.OK);
////        } catch (Throwable t) {
////            logger.error("", t);
////            return new OperationException(t.getMessage()).response();
////        }
////    }
//
////    @PostMapping("/{id}/result")
////    public ResponseEntity<?> backtestResult(@PathVariable Integer id, @RequestBody Map<String, Object> resultJson) throws Exception {
////        logger.debug("[BACKTEST RESULT] id: {}", id);
////        taskService.registerBackTestResult(id, resultJson);
////        return new ResponseEntity<>(resultJson, HttpStatus.OK);
////    }
////
////    @GetMapping("/getBacktestResult")
////    public ResponseEntity<?> getBackTestResult(){
////        return success(taskService.getBackTestResult());
////    }
//
//}