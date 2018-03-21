package io.gncloud.coin.server.api;

import io.gncloud.coin.server.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/*
 * create joonwoo 2018. 3. 21.
 * 
 */
@RestController
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/runTask")
    public ResponseEntity<?> runTask(@RequestBody String userId,
                                     @RequestBody String exchangeName,
                                     @RequestBody String baseCurrency,
                                     @RequestBody float capitalBase,
                                     @RequestBody boolean simulationOrder,
                                     @RequestBody Date start,
                                     @RequestBody Date end,
                                     @RequestBody String dataFrequency,
                                     @RequestBody String live){
        try{

            if(live != null && "true".equalsIgnoreCase(live)){
                taskService.liveMode(userId, exchangeName, baseCurrency, capitalBase, simulationOrder);
            }else{
                taskService.backTestMode(userId, exchangeName, baseCurrency, capitalBase, dataFrequency, start, end);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}