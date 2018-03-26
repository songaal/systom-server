package io.gncloud.coin.server.api;

import io.gncloud.coin.server.exception.AbstractException;
import io.gncloud.coin.server.exception.ParameterException;
import io.gncloud.coin.server.model.Algo;
import io.gncloud.coin.server.model.User;
import io.gncloud.coin.server.service.AlgosService;
import io.gncloud.coin.server.service.AuthService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AlgosController extends AbstractController{

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(AlgosController.class);

    @Autowired
    private AlgosService algosService;

    @Autowired
    private AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<?> getAlgo(@RequestHeader(name = "X-coincloud-user-id") String token) throws ParameterException {
        User registerUser = authService.findTokenByUser(token);

        List<Algo> registerAlgoList = algosService.findUserIdByAlgo(registerUser.getUserId());

        return success(registerAlgoList);
    }

    @GetMapping("/{algoId}")
    public @ResponseBody String getAlgoCode(@PathVariable String algoId) {
        try {
            Algo registerAlgo = algosService.getAlgo(algoId);
            if(registerAlgo == null || registerAlgo.getCode() == null){
                throw new ParameterException("algoId");
            }
            return registerAlgo.getCode();
        } catch (AbstractException e){
            logger.error("", e);
            return "no code";
        }
    }

    @PostMapping
    public ResponseEntity<?> createAlgo(@RequestBody Algo createAlgo) {
        try {
            Algo registerAlgo = algosService.insertAlgo(createAlgo.getUserId(), createAlgo.getCode());
            return success(registerAlgo);
        } catch (AbstractException e){
            logger.error("", e);
            return e.response();
        }
    }

    @PutMapping("/{algoId}")
    public ResponseEntity<?> updateAlgo(@PathVariable String algoId, @RequestBody Algo algo) {
        try {
            Algo registerAlgo = algosService.updateAlgo(algoId, algo.getCode());
            return success(registerAlgo);
        } catch (AbstractException e){
            logger.error("", e);
            return e.response();
        }
    }

}