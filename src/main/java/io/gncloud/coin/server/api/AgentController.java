package io.gncloud.coin.server.api;

import io.gncloud.coin.server.exception.AbstractException;
import io.gncloud.coin.server.model.Agent;
import io.gncloud.coin.server.service.AgentService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 * create joonwoo 2018. 4. 13.
 * 
 */
@RestController
@RequestMapping(value = "/v1/agents", produces = "application/json")
public class AgentController extends AbstractController {

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
}