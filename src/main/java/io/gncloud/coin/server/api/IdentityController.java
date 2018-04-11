package io.gncloud.coin.server.api;

import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
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

    public final static String  ACCESS_TOKEN = "ACCESS_TOKEN";
    public final static String  REFRESH_TOKEN = "REFRESH_TOKEN";


    @Autowired
    private IdentityService identityService;

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(IdentityController.class);

    /**
     * 로그인 수행
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(HttpServletResponse response, @RequestParam String id, @RequestParam String password) {
        logger.debug("id DEBUG : {}", id);
        try {
            Map<String, String> payload = identityService.login(id, password);
            String accessToken = null;
            String refreshToken = null;
            Integer expiresIn = null;
            String idToken = null;
//            if (StringUtils.isBlank(result.getChallengeName())) {
//                AuthenticationResultType authResult = result.getAuthenticationResult();
//                logger.info("authResult > {}", authResult);
//                accessToken = authResult.getAccessToken();
//                expiresIn = authResult.getExpiresIn();
//                idToken = authResult.getIdToken(); //사용자 정보.
//                refreshToken = authResult.getRefreshToken();
//                authResult.getTokenType();
//            } else {
//                //TODO 패스워드 변경등 요청에 따라..
//
//            }
//
//            if(accessToken != null) {
//                updateCredentialCookies(response, accessToken, refreshToken, expiresIn);
//            }

            return new ResponseEntity<>(idToken, HttpStatus.OK);
        } catch (Throwable t) {
            logger.error("", t);
            throw t;
        }
    }

    /**
     * 로그아웃
     * @param id 사용자 아이디
     * @param token 발급 토큰
     * @return OK
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseEntity<?> logout(@RequestHeader("Mgb-User") String id, @RequestHeader("Mgb-Token") String token) {
        logger.debug("LOGOUT USER DEBUG : {}", id);
        try {
            identityService.logout(id, token);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @RequestMapping(value = "/admin", method = RequestMethod.PUT)
//    public ResponseEntity<?> updateAdmin(@RequestHeader("Mgb-User") String id, @RequestParam String name, @RequestParam String email, @RequestParam(required = false) String password) throws AuthorizationException, UserNotFoundException {
//        if (!id.equals(g.getAdminId())) {
//            throw new AuthorizationException("Not admin user error");
//        }
//        Admin admin = new Admin();
//        admin.setUserS(g.getAdminId());
//        admin.setEmailS(email);
//        admin.setNameS(name);
//        admin.setPasswordS(password);
//        try {
//            identityService.updateAdmin(admin);
//            return new ResponseEntity<Token>(HttpStatus.OK);
//        } catch (Exception e) {
//            logger.error("", e);
//            return new ResponseEntity<Token>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

//    @RequestMapping(value = "/me", method = RequestMethod.GET)
//    public ResponseEntity<?> info(@RequestHeader(name = "Mgb-User") String user){
//        try {
//
//            if(g.getAdminId().equals(user)){
//                Admin admin = identityService.adminInfo();
//                return new  ResponseEntity<> (admin, HttpStatus.OK);
//            }else{
//                User userInfo = null;
//                if(Env.isIgnoreLogin()) {
//                    userInfo = new User();
//                    userInfo.setUser(user);
//
//                } else {
//                    userInfo = identityService.userInfo(user);
//                    String countryCd = userInfo.getCountryCd();
//
//                    if(countryCd != null && !"Z9".equals(countryCd) && !"****".equals(countryCd)){
//                        for(String countyCode : Locale.getISOCountries()){
//                            if(countyCode.equals(countryCd)){
//                                Locale locale = new Locale("en", countyCode);
//                                userInfo.setCountryCd(locale.getDisplayCountry());
//                                break;
//                            }
//                        }
//                    }
//                }
//
//
//                return new  ResponseEntity<> (userInfo, HttpStatus.OK);
//            }
//        } catch (UserNotFoundException e) {
//            return new  ResponseEntity<> (HttpStatus.BAD_REQUEST);
//        } catch (Exception e){
//            logger.error("", e);
//            return new  ResponseEntity<> (HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    /**
     * 세션확인용도의 api
     * @return OK
     */
    @RequestMapping(value = "/validate", method = RequestMethod.GET)
    public ResponseEntity<?> validate(){
        return new ResponseEntity<>(HttpStatus.OK);
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