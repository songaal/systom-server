package io.systom.coin.api;

import io.systom.coin.exception.AbstractException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.model.Card;
import io.systom.coin.model.UserAttribute;
import io.systom.coin.service.CardService;
import io.systom.coin.service.UserAttributeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/paidMemberShip")
public class UserAttributeController extends AbstractController{
    private static Logger logger = LoggerFactory.getLogger(UserAttributeController.class);

    @Autowired
    private UserAttributeService userAttributeService;

    @GetMapping
    public ResponseEntity<?> getPaidPlan(@RequestAttribute String userId) {
        try {
            UserAttribute userAttribute = userAttributeService.getPaidPlan(userId);
            return success(userAttribute);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            return new OperationException(t.getMessage()).response();
        }
    }

    @PostMapping
    public ResponseEntity<?> joinPaidPlan(@RequestAttribute String userId,
                                          @RequestBody UserAttribute userAttribute) {
        try {
            userAttribute.setUserId(userId);
            userAttributeService.insertPaidPlan(userAttribute);
            return success();
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            return new OperationException(t.getMessage()).response();
        }
    }

    @PutMapping
    public ResponseEntity<?> updatePaidPlan(@RequestAttribute String userId,
                                            @RequestParam String action,
                                            @RequestBody UserAttribute userAttribute) {
        try {
            userAttribute.setUserId(userId);
            if ("delete".equalsIgnoreCase(action)) {
                userAttribute.setPaidUser(false);
                userAttribute.setCanceled(true);
                userAttributeService.updateCancelPlan(userAttribute);
            } else if ("reUse".equalsIgnoreCase(action)) {
                userAttribute.setPaidUser(true);
                userAttribute.setCanceled(false);
                userAttribute.setDueDate(null);
                userAttributeService.updateCancelPlan(userAttribute);
            }
            return success(userAttribute);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            return new OperationException(t.getMessage()).response();
        }
    }


}
