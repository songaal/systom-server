package io.gncloud.coin.server.api;

import io.gncloud.coin.server.service.IdentityService;
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
    private List<String> exceptUrl = Arrays.asList( "/auth/login", "/auth/signUp", "/ping", "/ws", "/error", "/auth/changeTempPassword" ,"/v1/tasks/waitRunBackTestTask");


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

            if (exceptUrl.contains(url)) {
                return true;
            }

            String accessToken = getCookieValue(request, IdentityController.ACCESS_TOKEN);
            if (accessToken == null) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            }

            boolean isValid = identityService.isValidAccessToken(accessToken);

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


}