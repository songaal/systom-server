package io.gncloud.coin.server.api;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;
import io.gncloud.coin.server.service.IdentityService;
import io.gncloud.coin.server.utils.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


/**
 * 인증 API 컨트롤러
 */
@RestController
@RequestMapping("/auth")
public class IdentityController {

    public final static String ACCESS_TOKEN = "COINCLOUD-ACCESS-TOKEN";
    public final static String REFRESH_TOKEN = "COINCLOUD-REFRESH-TOKEN";
    public final static String SESSION_ID = "COINCLOUD-SESSION-ID";


    @Autowired
    private IdentityService identityService;

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(IdentityController.class);

    /**
     * 회원가입
     * */
    @RequestMapping(value = "/signUp", method = RequestMethod.POST)
    public ResponseEntity<?> signUp(HttpServletResponse response, @RequestParam String userId, @RequestParam String email) {
        AdminCreateUserResult result = identityService.signUp(userId, email);
        UserType user = result.getUser();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    /**
     * 로그인 수행
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(HttpServletResponse response, @RequestParam String userId, @RequestParam String password) {
        logger.debug("/login userId: {}", userId);
        try {
            AdminInitiateAuthResult initAuthResult = identityService.login(userId, password);
            AuthenticationResultType authResult = initAuthResult.getAuthenticationResult();
            //쿠키 업데이트
            updateCredentialCookies(response, authResult.getAccessToken(), authResult.getRefreshToken(), initAuthResult.getSession(), authResult.getExpiresIn());
            //사용자 정보
            Map<String, String> payload = identityService.parsePayload(authResult.getAccessToken());
            return new ResponseEntity<>(payload, HttpStatus.OK);
        } catch (Throwable t) {
            logger.error("login error", t);
            throw t;
        }
    }

    /**
     * 회원가입시 자동생성된 임시비번 변경
     */
    @RequestMapping(value = "/changeTempPassword", method = RequestMethod.POST)
    public ResponseEntity<?> changeTempPassword(HttpServletResponse response, @CookieValue(value = SESSION_ID) String session, @RequestParam String userId, @RequestParam String password) {
        AdminRespondToAuthChallengeResult respondAuthResult = identityService.changeTempPassword(session, userId, password);
        AuthenticationResultType authResult = respondAuthResult.getAuthenticationResult();
        //쿠키 업데이트
        updateCredentialCookies(response, authResult.getAccessToken(), authResult.getRefreshToken(), respondAuthResult.getSession(), authResult.getExpiresIn());
        //사용자 정보
        Map<String, String> payload = identityService.parsePayload(authResult.getAccessToken());
        return new ResponseEntity<>(payload, HttpStatus.OK);
    }

    /**
     * 로그아웃
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseEntity<?> logout(HttpServletResponse response, @RequestParam String userId, @CookieValue(value=ACCESS_TOKEN) String accessToken
            , @CookieValue(value=REFRESH_TOKEN) String refreshToken
            , @CookieValue(value=SESSION_ID) String sessionId) {
        logger.debug("LOGOUT USER : {}", userId);
        try {
            //쿠키 삭제
            updateCredentialCookies(response, accessToken, refreshToken, sessionId, 0);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void updateCredentialCookies(HttpServletResponse response, String accessToken, String refreshToken, String session, Integer expiresIn) {
        if(accessToken != null) {
            Cookie cookie = new Cookie(ACCESS_TOKEN, accessToken);
            cookie.setMaxAge(expiresIn);
            cookie.setPath("/");
            response.addCookie(cookie);

            if(refreshToken != null) {
                Cookie cookie2 = new Cookie(REFRESH_TOKEN, refreshToken);
                cookie2.setMaxAge(expiresIn + 300); //5분 더 살도록 한다.
                cookie2.setPath("/");
                response.addCookie(cookie2);
            }

            if(session != null) {
                Cookie cookie3 = new Cookie(SESSION_ID, refreshToken);
                cookie3.setMaxAge(expiresIn);
                cookie3.setPath("/");
                response.addCookie(cookie3);
            }
        }
    }
}