package io.gncloud.coin.server.service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;
import io.gncloud.coin.server.model.User;
import io.gncloud.coin.server.utils.CredentialsCache;
import io.gncloud.coin.server.utils.StringUtils;
import io.gncloud.coin.server.ws.WebSocketSessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 권한, 인증 및 세션 관리 서비스
 */
@Service
public class IdentityService {
    private static Logger logger = LoggerFactory.getLogger(IdentityService.class);


    //웹소켓
    private Map<String, ConcurrentSkipListSet<WebSocketSessionInfo>> subscriberMap;

    //토큰 캐시.
    private CredentialsCache tokenCache;

    private AWSCognitoIdentityProvider cognitoClient;

    @Autowired
    private String cognitoPoolId;
    @Autowired
    private String cognitoClientId;

    @PostConstruct
    public void init() {
        cognitoClient = AWSCognitoIdentityProviderClientBuilder.standard().build();
        tokenCache = new CredentialsCache(10000);
    }

    public Map<String, ConcurrentSkipListSet<WebSocketSessionInfo>> getSubscriberMap() {
        return subscriberMap;
    }

    /**
     * 로그인
     *
     * */
    public AdminInitiateAuthResult login(String emailAddress, String password) {
        if (StringUtils.isBlank(emailAddress) || StringUtils.isBlank(password)) {
//            reportResult(response, Constants.ResponseMessages.INVALID_REQUEST);
            return null;
        }

        logger.debug("authenticating {}", emailAddress);

        try {
            Map<String, String> authParams = new HashMap<String, String>();
            authParams.put("USERNAME", emailAddress);
            authParams.put("PASSWORD", password);

            AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest()
                    .withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                    .withAuthParameters(authParams)
                    .withClientId(cognitoClientId)
                    .withUserPoolId(cognitoPoolId);

            AdminInitiateAuthResult authResponse = cognitoClient.adminInitiateAuth(authRequest);
            return authResponse;
        } catch (UserNotFoundException ex) {
            logger.debug("not found: {}", emailAddress);
//            reportResult(response, Constants.ResponseMessages.NO_SUCH_USER);
        } catch (NotAuthorizedException ex) {
            logger.debug("invalid credentials: {}", emailAddress);
//            reportResult(response, Constants.ResponseMessages.NO_SUCH_USER);
        } catch (TooManyRequestsException ex) {
            logger.warn("caught TooManyRequestsException, delaying then retrying");
//            ThreadUtil.sleepQuietly(250);
//            doPost(request, response);
        }
        return null;
    }

    /**
     * 회원가입
     *
     * */
    public AdminCreateUserResult signUp(String emailAddress) {
        if (StringUtils.isBlank(emailAddress)) {
//            reportResult(response, Constants.ResponseMessages.INVALID_REQUEST);
            return null;
        }

        logger.debug("creating user {}", emailAddress);

        try {
            AdminCreateUserRequest cognitoRequest = new AdminCreateUserRequest()
                    .withUserPoolId(cognitoPoolId)
                    .withUsername(emailAddress)
                    .withUserAttributes(
                            new AttributeType()
                                    .withName("email")
                                    .withValue(emailAddress),
                            new AttributeType()
                                    .withName("email_verified")
                                    .withValue("true"))
                    .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL)
                    .withForceAliasCreation(Boolean.FALSE);

            AdminCreateUserResult result = cognitoClient.adminCreateUser(cognitoRequest);
            return result;
        } catch (UsernameExistsException ex) {
            logger.debug("user already exists: {}", emailAddress);
//            reportResult(response, Constants.ResponseMessages.USER_ALREADY_EXISTS);
        } catch (TooManyRequestsException ex) {
            logger.warn("caught TooManyRequestsException, delaying then retrying");
//            ThreadUtil.sleepQuietly(250);
//            doPost(request, response);
        }
        return null;
    }


//    protected void updateCredentialCookies(AuthenticationResultType authResult) {
//        tokenCache.addToken(authResult.getAccessToken());
//
//        Cookie accessTokenCookie = new Cookie(ACCESS_TOKEN, authResult.getAccessToken());
////        response.addCookie(accessTokenCookie);
//
//        if (!StringUtils.isBlank(authResult.getRefreshToken())) {
//            Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN, authResult.getRefreshToken());
////            response.addCookie(refreshTokenCookie);
//        }
//    }


    public User findTokenByUser(String token) {
        return null;
    }

    public void logout(String id, String token) {

    }
}
