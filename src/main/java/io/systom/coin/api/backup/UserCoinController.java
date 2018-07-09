//package io.gncloud.coin.server.api;
//
//import AbstractException;
//import User;
//import io.gncloud.coin.server.service.UserCoinService;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
///*
// * create joonwoo 2018. 6. 11.
// *
// */
//@RestController
//@RequestMapping("/v1/coin")
//public class UserCoinController extends AbstractController{
//
//    private static org.slf4j.Logger logger = LoggerFactory.getLogger(UserCoinController.class);
//
//    @Autowired
//    private UserCoinService userCoinService;
//
//    @PostMapping
//    public ResponseEntity<?> chargeAmount(@RequestAttribute("userId") String userId, @RequestBody User user) {
//        try {
//            User registerUser = userCoinService.updateAmount(userId, user.getInvestCash());
//            return success(registerUser);
//        } catch (AbstractException e) {
//            logger.error("", e);
//            return e.response();
//        }
//    }
//
//    @GetMapping("/me")
//    public ResponseEntity<?> getInvestCash(@RequestAttribute("userId") String userId) {
//        User registerUser = userCoinService.getUserCoin(userId);
//        return success(registerUser);
//    }
//
//}