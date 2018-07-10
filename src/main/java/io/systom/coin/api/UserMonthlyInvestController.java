package io.systom.coin.api;

import io.systom.coin.exception.AbstractException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.model.backup.UserMonthlyInvest;
import io.systom.coin.service.UserMonthInvestService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
 * create joonwoo 2018. 7. 10.
 * 
 */
@RestController
@RequestMapping("/v1/userMonthlyInvest")
public class UserMonthlyInvestController extends AbstractController {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(UserMonthlyInvestController.class);

    @Autowired
    private UserMonthInvestService userMonthInvestService;

    @GetMapping
    public ResponseEntity<?> retrieveUserMonthInvest(@RequestAttribute String userId) {
        try {
            List<UserMonthlyInvest> userMonthlyInvestList = userMonthInvestService.retrieveUserMonthInvestList(userId);
            return success(userMonthlyInvestList);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("Throwable: ", t);
            return new OperationException().response();
        }
    }

}