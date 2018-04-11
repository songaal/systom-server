package io.gncloud.coin.server.api;

import io.gncloud.coin.server.service.IdentityService;
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


/**
 * 권한 인터셉터
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private org.slf4j.Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    @Autowired
    private IdentityService identityService;
    //ip 내부망은 true  , 그외 false

    // 토큰 확인 안하는 URL
    private List<String> exceptUrl = Arrays.asList( "/auth/login", "/auth/signUp", "/ping", "/ws", "/error" );


    /**
     * api 요청시 권환 확인
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String url = httpServletRequest.getRequestURI();

        if(exceptUrl.contains(url)){
            return true;
        }

        String accessToken = getCookieValue(httpServletRequest, IdentityController.ACCESS_TOKEN);
        if(accessToken == null) {
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        boolean isValid = identityService.isValidAccessToken(accessToken);

        if(!isValid) {
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        return true;
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