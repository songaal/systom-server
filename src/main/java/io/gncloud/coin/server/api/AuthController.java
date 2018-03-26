package io.gncloud.coin.server.api;

import io.gncloud.coin.server.exception.ParameterException;
import io.gncloud.coin.server.model.User;
import io.gncloud.coin.server.service.AuthService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 * create joonwoo 2018. 3. 24.
 * 
 */
@RestController
@RequestMapping("/v1/auth")
public class AuthController extends AbstractController{

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user){
        try {
            return success(authService.loginProcess(user));
        } catch (ParameterException e) {
            logger.error("", e);
            return e.response();
        }
    }




}