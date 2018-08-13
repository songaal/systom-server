package io.systom.coin.api;

import io.systom.coin.exception.AbstractException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.exception.ParameterException;
import io.systom.coin.model.Invitation;
import io.systom.coin.service.InvitationService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * create joonwoo 2018. 8. 10.
 * 
 */
@RestController
@RequestMapping("/invitations")
public class InvitationController extends AbstractController{
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(InvitationController.class);

    @Autowired
    private InvitationService invitationService;

    @PostMapping
    public ResponseEntity<?> createInvitation(@RequestAttribute String userId) {
        try {
            Invitation invitation = invitationService.createInvitation(userId);
            return success(invitation);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("", t);
            return new OperationException().response();
        }
    }

    @GetMapping
    public ResponseEntity<?> selectInvitation(@RequestParam(required = false) String ref,
                                              @RequestAttribute(required = false) String userId) {
        try {
            if (ref != null) {
                Invitation invitation = invitationService.findInvitationByRefCode(ref);
                if (invitation == null) {
                    throw new ParameterException("ref");
                }
                return success(invitation);
            } else if (userId != null) {
                List<Invitation> invitations = invitationService.selectInvitationList(userId);
                Map<String, Object> response = new HashMap<>();
                response.put("invitations", invitations);
                response.put("maxInvitationSize", invitationService.getUserMaxInvitationSize(userId));
                return success(response);
            } else {
                return success();
            }
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable t) {
            logger.error("", t);
            return new OperationException().response();
        }
    }

}