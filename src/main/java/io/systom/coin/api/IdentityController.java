package io.systom.coin.api;

import com.amazonaws.services.cognitoidp.model.*;
import io.systom.coin.exception.AbstractException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.model.ExchangeKey;
import io.systom.coin.model.Identity;
import io.systom.coin.model.UserNotification;
import io.systom.coin.service.ExchangeService;
import io.systom.coin.service.IdentityService;
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

    public final static String ACCESS_TOKEN = "COINCLOUD-ACCESS-TOKEN";
    public final static String REFRESH_TOKEN = "COINCLOUD-REFRESH-TOKEN";
    public final static String ID_TOKEN = "COINCLOUD-ID-TOKEN";

    @Autowired
    private IdentityService identityService;
    @Autowired
    private ExchangeService exchangeService;

    /**
     * 회원가입
     * */
    @RequestMapping(value = "/signUp", method = RequestMethod.POST)
    public ResponseEntity<?> signUp(@RequestBody Identity identity) throws OperationException {
        AdminCreateUserResult result = identityService.signUp(identity.getUserId(), identity.getEmail());
        UserType user = result.getUser();
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
        return new ResponseEntity<>(payload, HttpStatus.OK);
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