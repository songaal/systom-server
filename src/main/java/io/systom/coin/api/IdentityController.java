package io.systom.coin.api;

import com.amazonaws.services.cognitoidp.model.*;
import io.systom.coin.exception.AbstractException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.exception.RequestException;
import io.systom.coin.model.*;
import io.systom.coin.service.ExchangeService;
import io.systom.coin.service.IdentityService;
import io.systom.coin.service.InvitationService;
import io.systom.coin.service.UserAttributeService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 인증 API 컨트롤러
 */
@RestController
@RequestMapping("/auth")
public class IdentityController {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(IdentityController.class);

    public final static String ACCESS_TOKEN = "SYSTOM-ACCESS-TOKEN";
    public final static String REFRESH_TOKEN = "SYSTOM-REFRESH-TOKEN";
    public final static String ID_TOKEN = "SYSTOM-ID-TOKEN";

    @Autowired
    private IdentityService identityService;
    @Autowired
    private ExchangeService exchangeService;
    @Autowired
    private InvitationService invitationService;
    @Autowired
    private UserAttributeService userAttributeService;
    /**
     * 회원가입
     * */
    @RequestMapping(value = "/signUp", method = RequestMethod.POST)
    public ResponseEntity<?> signUp(@RequestBody Identity identity) throws OperationException {
        if (identityService.getGestUserId().equalsIgnoreCase(identity.getUserId())) {
            new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Invitation invitation = invitationService.findInvitationByRefCode(identity.getRef());
        if (invitation == null || !invitation.getStatus()) {
            new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        AdminCreateUserResult result = identityService.signUp(identity.getUserId(), identity.getEmail());
        UserType user = result.getUser();
        invitationService.updateRefUser(identity.getUserId(), identity.getRef());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    /**
     * 로그인 수행
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(HttpServletResponse response, @RequestBody Identity identity) {
        try {
            AdminInitiateAuthResult initAuthResult = identityService.login(identity.getUserId(), identity.getPassword());
            AuthenticationResultType authResult = initAuthResult.getAuthenticationResult();
            if(authResult != null) {
                //쿠키 업데이트
                identityService.updateCredentialCookies(response, authResult.getAccessToken(), authResult.getRefreshToken(), authResult.getIdToken(), authResult.getExpiresIn());
                //사용자 정보
                Map<String, String> payload = identityService.parsePayload(authResult.getAccessToken());
                return new ResponseEntity<>(payload, HttpStatus.OK);
            } else {
                Map<String, String> result = new HashMap<>();
                result.put("challengeName", initAuthResult.getChallengeName());
                result.put("session", initAuthResult.getSession());
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (NotAuthorizedException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Throwable t) {
            logger.error("login error", t);
            throw t;
        }
    }

    /**
     * 회원가입시 자동생성된 임시비번 변경
     */
    @RequestMapping(value = "/changeTempPassword", method = RequestMethod.POST)
    public ResponseEntity<?> changeTempPassword(HttpServletResponse response, @RequestBody Identity identity) {
        AdminRespondToAuthChallengeResult respondAuthResult = identityService.changeTempPassword(identity.getSession(), identity.getUserId(), identity.getPassword());
        AuthenticationResultType authResult = respondAuthResult.getAuthenticationResult();
        //쿠키 업데이트
        identityService.updateCredentialCookies(response, authResult.getAccessToken(), authResult.getRefreshToken(), authResult.getIdToken(), authResult.getExpiresIn());
        //사용자 정보
        Map<String, String> payload = identityService.parsePayload(authResult.getAccessToken());
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    /**
     * 로그아웃
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseEntity<?> logout(HttpServletResponse response) {
        try {
            //쿠키 삭제
            identityService.updateCredentialCookies(response, "", "", "",0);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 회원정보 조회
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> userInfo(@CookieValue(value = ACCESS_TOKEN) String accessToken, @CookieValue(value = ID_TOKEN) String idToken, @RequestAttribute String userId) {
        Map<String, String> payload = identityService.parsePayload(idToken);
        payload.putAll(identityService.parsePayload(accessToken));
        payload.put("isManager", String.valueOf(identityService.isManager(userId)));
        payload.put("isPaidPlan", String.valueOf(userAttributeService.isPaidPlan(userId)));
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    /**
     * 비밀번호 분실 ( 메일확인 )
     */
    @RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPassword forgotPassword) {
        try {
            if (ForgotPassword.ACTIONS.confirm.name().equalsIgnoreCase(forgotPassword.getAction())) {
                ForgotPasswordResult forgotPasswordResult = identityService.forgotPassword(forgotPassword);
                String destination = forgotPasswordResult.getCodeDeliveryDetails().getDestination();
                return new ResponseEntity<>(destination, HttpStatus.OK);
            } else if (ForgotPassword.ACTIONS.reset.name().equalsIgnoreCase(forgotPassword.getAction())) {
                logger.debug("confirmForgotPassword: {}", forgotPassword);
                identityService.confirmForgotPassword(forgotPassword);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                logger.debug("not found action: {}", forgotPassword.getAction());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (RequestException e){
            return e.response();
        } catch (Exception e) {
            logger.error("", e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 비밀번호 변경
     */
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public ResponseEntity<?> changePassword(@CookieValue(ACCESS_TOKEN) String accessToken,
                                            @RequestBody ChangePassword changePassword) {
        try {
            changePassword.setAccessToken(accessToken);
            identityService.changePassword(changePassword);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RequestException e){
            return e.response();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * 거래소 키 조회
     */
    @RequestMapping(value = "/exchangeKey", method = RequestMethod.GET)
    public ResponseEntity<?> exchange(@RequestAttribute String userId) {
        try {
            List<ExchangeKey> exchangeKeys = exchangeService.selectExchangeKeys(userId);
            return new ResponseEntity<>(exchangeKeys, HttpStatus.OK);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        }
    }

    /**
     * 거래소 키 추가
     */
    @RequestMapping(value = "/exchangeKey", method = RequestMethod.POST)
    public ResponseEntity<?> insertExchangeKey(@RequestAttribute String userId, @RequestBody ExchangeKey exchangeKey) {
        try {
            exchangeKey.setUserId(userId);
            exchangeService.insertExchangeKey(exchangeKey);
            ExchangeKey registerKey = exchangeService.selectExchangeKey(exchangeKey);
            return new ResponseEntity<>(registerKey, HttpStatus.OK);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        }
    }

    /**
     * 거래소 키 삭제
     */
    @RequestMapping(value = "/exchangeKey/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteExchangeKey(@RequestAttribute String userId, @PathVariable String id) {
        try {
            ExchangeKey exchangeKey = new ExchangeKey();
            exchangeKey.setId(Integer.parseInt(id));
            exchangeKey.setUserId(userId);
            ExchangeKey registerKey = exchangeService.deleteExchangeKey(exchangeKey);
            return new ResponseEntity<>(registerKey, HttpStatus.OK);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        }
    }

    /**
     * 텔레그램 아이디 수정
     */
    @RequestMapping(value = "/telegram", method = RequestMethod.PUT)
    public ResponseEntity<?> updateTelegram(@RequestAttribute String userId, @RequestBody Map<String, Object> body) {
        try {
            String telegramServiceUser = String.valueOf(body.get("telegramServiceUser"));
            identityService.updateTelegramUserId(userId, telegramServiceUser);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (AbstractException e) {
            logger.error("", e);
            return e.response();
        }
    }

    /**
     * 텔레그램 아이디 수정
     */
    @RequestMapping(value = "/telegram", method = RequestMethod.GET)
    public ResponseEntity<?> getUserNotification(@RequestAttribute String userId) {
        List<UserNotification> notificationList = identityService.getUserNotification(userId);
        return new ResponseEntity<>(notificationList, HttpStatus.OK);
    }

}