package io.gncloud.coin.server.api;

import io.gncloud.coin.server.exception.ParameterException;
import io.gncloud.coin.server.service.AlgosService;
import io.gncloud.coin.server.service.TasksService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 * create joonwoo 2018. 3. 21.
 * 
 */
@RestController
@RequestMapping("/v1/tasks")
public class TasksController {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(TasksController.class);

    @Autowired
    private TasksService taskService;

    @Autowired
    private AlgosService algosService;

    @PostMapping
    public ResponseEntity<?> createTask(@RequestParam String algoId,
                                     @RequestParam String exchangeName,
                                     @RequestParam String baseCurrency,
                                     @RequestParam String capitalBase,
                                     @RequestParam String live,
                                     @RequestParam(required = false) String simulationOrder,
                                     @RequestParam(required = false) String start,
                                     @RequestParam(required = false) String end,
                                     @RequestParam(required = false) String dataFrequency){
        try{

            if(live != null && "true".equalsIgnoreCase(live)){
                taskService.liveMode(algoId, exchangeName, baseCurrency, Float.parseFloat(capitalBase), Boolean.parseBoolean(simulationOrder));
            }else{
                taskService.backTestMode(algoId, exchangeName, baseCurrency, Float.parseFloat(capitalBase), dataFrequency, start, end);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ParameterException e){
            logger.error("Bad Request:", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch(Exception e){
            logger.error("System Error:", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}