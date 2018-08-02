package io.systom.coin.api;

import io.systom.coin.service.IdentityService;
import io.systom.coin.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
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

    @Autowired
    private TaskService taskService;
    //ip 내부망은 true  , 그외 false

    // 토큰 확인 안하는 URL
    private List<String> exceptUrl = Arrays.asList( "/auth/login", "/auth/signUp", "/ping", "/error", "/auth/changeTempPassword", "/auth/forgotPassword");

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

            Object urlTemplateVariables = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            if (urlTemplateVariables != null) {
                Map<String, String> pathVariables = (Map<String, String>) urlTemplateVariables;
                if (taskService.isWaitTask(pathVariables.get("taskId")) != null) {
                    return true;
                }
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
//            throw e;
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    String getCookieValue(HttpServletRequest request , String name) {
        if (request.getCookies() == null) {
            return null;
        }
        for(Cookie cookie : request.getCookies()) {

            if(cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }

        return null;
    }

}