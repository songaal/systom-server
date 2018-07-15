package io.systom.coin.api;

import io.systom.coin.service.IdentityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 권한 인터셉터
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    @Autowired
    private IdentityService identityService;
    //ip 내부망은 true  , 그외 false

    // 토큰 확인 안하는 URL
    private List<String> exceptUrl = Arrays.asList( "/auth/login", "/auth/signUp", "/ping", "/ws", "/error", "/auth/changeTempPassword");

    private Map<String, String> accessKeyUrl = new HashMap<>();

    /**
     * api 요청시 권환 확인
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String url = request.getRequestURI();
            String accessKey = request.getHeader("accessKey");
            if (accessKey != null && isAccessKeyUrl(url, accessKey)) {
                return true;
            }
            if (exceptUrl.contains(url)) {
                return true;
            }

            String accessToken = getCookieValue(request, IdentityController.ACCESS_TOKEN);
            if (accessToken == null) {
                logger.debug("expired accessToken");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            }

            String refreshToken = getCookieValue(request, IdentityController.REFRESH_TOKEN);
            String idToken = getCookieValue(request, IdentityController.ID_TOKEN);
            identityService.refreshToken(response, accessToken, refreshToken, idToken);

            boolean isValid = identityService.isValidAccessToken(accessToken);

            logger.debug("isValid: {}, Token: {}", isValid, accessToken);

            if (!isValid) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            }

            Map<String, String> payload = identityService.parsePayload(accessToken);
            String userId = payload.get("username");
            request.setAttribute("userId", userId);

            return true;
        } catch (Exception e) {
            logger.error("error in authInterceptor.", e);
            throw e;
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    String getCookieValue(HttpServletRequest request , String name) {
        for(Cookie cookie : request.getCookies()) {

            if(cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }

        return null;
    }

    public void addAccessKeyUrl (String url, String accessKey) {
        this.accessKeyUrl.put(url, accessKey);
    }

    public boolean isAccessKeyUrl (String url, String accessKey) {
        if (this.accessKeyUrl.get(url) != null && this.accessKeyUrl.get(url) == accessKey) {
            this.accessKeyUrl.remove(url);
            return true;
        } else {
            return false;
        }
    }
}