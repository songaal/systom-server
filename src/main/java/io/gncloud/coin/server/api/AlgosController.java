package io.gncloud.coin.server.api;

import io.gncloud.coin.server.exception.ParameterException;
import io.gncloud.coin.server.model.Algo;
import io.gncloud.coin.server.service.AlgosService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * create joonwoo 2018. 3. 22.
 * 
 */
@Controller
@RequestMapping("/v1/algos")
public class AlgosController {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(AlgosController.class);

    @Autowired
    private AlgosService algosService;

    @GetMapping("/me")
    public ResponseEntity<?> getAlgo(@RequestHeader String userId){
        try {
            List<Algo> registerAlgoList = algosService.findUserIdByAlgo(userId);
            return new ResponseEntity<>(registerAlgoList, HttpStatus.OK);
        } catch (ParameterException e){
            logger.error("", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            logger.error("", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{algoId}")
    public @ResponseBody String getAlgoSource(@PathVariable String algoId){
        try {
            Algo registerAlgo = algosService.getAlgo(algoId);
            return registerAlgo.getSource();
        } catch (Exception e){
            logger.error("", e);
            return "";
        }
    }

    @PostMapping
    public ResponseEntity<?> createAlgo(@RequestParam String userId, @RequestParam String source) {
        try {

            Algo registerAlgo = algosService.insertAlgo(userId, source);

            return new ResponseEntity<>(registerAlgo, HttpStatus.OK);
        } catch (ParameterException e){
            logger.error("", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e){
            logger.error("", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{algoId}")
    public ResponseEntity<?> updateAlgo(@PathVariable String algoId, @RequestParam String source) {
        try {

            Algo registerAlgo = algosService.updateAlgo(algoId, source);

            return new ResponseEntity<>(registerAlgo, HttpStatus.OK);
        } catch (ParameterException e){
            logger.error("", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e){
            logger.error("", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}