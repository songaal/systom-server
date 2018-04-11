package io.gncloud.coin.server.api;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;
import io.gncloud.coin.server.model.Identity;
import io.gncloud.coin.server.service.IdentityService;
import io.gncloud.coin.server.utils.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


/**
 * 인증 API 컨트롤러
 */
@RestController
@RequestMapping("/auth")
public class IdentityController {

    public final static String ACCESS_TOKEN = "COINCLOUD-ACCESS-TOKEN";
    public final static String REFRESH_TOKEN = "COINCLOUD-REFRESH-TOKEN";


    @Autowired
    private IdentityService identityService;

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(IdentityController.class);

    /**
     * 회원가입
     * */
    @RequestMapping(value = "/signUp", method = RequestMethod.POST)
    public ResponseEntity<?> signUp(HttpServletResponse response, @RequestBody Identity identity) {
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
                updateCredentialCookies(response, authResult.getAccessToken(), authResult.getRefreshToken(), authResult.getExpiresIn());
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
        updateCredentialCookies(response, authResult.getAccessToken(), authResult.getRefreshToken(), authResult.getExpiresIn());
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
            updateCredentialCookies(response, "", "", 0);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void updateCredentialCookies(HttpServletResponse response, String accessToken, String refreshToken, Integer expiresIn) {
        if(accessToken != null) {
            Cookie cookie = new Cookie(ACCESS_TOKEN, accessToken);
            cookie.setMaxAge(expiresIn);
            cookie.setPath("/");
            response.addCookie(cookie);

            if(refreshToken != null) {
                Cookie cookie2 = new Cookie(REFRESH_TOKEN, refreshToken);
                cookie2.setMaxAge(expiresIn);
                cookie2.setPath("/");
                response.addCookie(cookie2);
            }
        }
    }
}