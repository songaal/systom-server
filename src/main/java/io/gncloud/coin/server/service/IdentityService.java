package io.gncloud.coin.server.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.model.Credentials;
import com.amazonaws.services.cognitoidentity.model.GetIdRequest;
import com.amazonaws.services.cognitoidentity.model.GetIdResult;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public SignUpResult signUp(String userId, String password, String emailAddress) {
        AnonymousAWSCredentials awsCreds = new AnonymousAWSCredentials();
        AWSCognitoIdentityProvider cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(Regions.fromName("ap-northeast-2"))
                .build();

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setClientId(cognitoClientId);
        signUpRequest.setUsername(userId);
        signUpRequest.setPassword(password);
        List<AttributeType> list = new ArrayList<>();

        AttributeType attributeType1 = new AttributeType();
        attributeType1.setName("email");
        attributeType1.setValue(emailAddress);
        list.add(attributeType1);

        signUpRequest.setUserAttributes(list);

        try {
            SignUpResult result = cognitoIdentityProvider.signUp(signUpRequest);
            return result;
        } catch (Exception e) {
            logger.error("", e);
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

    /**
     * Verify the verification code sent on the user phone.
     *
     * @param username User for which we are submitting the verification code.
     * @param code     Verification code delivered to the user.
     * @return if the verification is successful.
     */
    public ConfirmSignUpResult VerifyAccessCode(String username, String code) {
        ConfirmSignUpRequest confirmSignUpRequest = new ConfirmSignUpRequest();
        confirmSignUpRequest.setUsername(username);
        confirmSignUpRequest.setConfirmationCode(code);
        confirmSignUpRequest.setClientId(cognitoClientId);

        logger.debug("username=" + username);
        logger.debug("code=" + code);
        logger.debug("clientid=" + cognitoClientId);

        try {
            ConfirmSignUpResult confirmSignUpResult = cognitoClient.confirmSignUp(confirmSignUpRequest);
            logger.debug("confirmSignupResult={}", confirmSignUpResult);
            return confirmSignUpResult;
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }

    /**
     //         * Returns the AWS credentials
     //         *
     //         * @param accesscode access code
     //         * @return returns the credentials based on the access token returned from the user pool.
     //         */
//    public Credentials GetCredentials(String accesscode) {
//        Credentials credentials = null;
//
//        try {
//            Map<String, String> httpBodyParams = new HashMap<String, String>();
//            httpBodyParams.put(Constants.TOKEN_GRANT_TYPE, Constants.TOKEN_GRANT_TYPE_AUTH_CODE);
//            httpBodyParams.put(Constants.DOMAIN_QUERY_PARAM_CLIENT_ID, CLIENTAPP_ID);
//            httpBodyParams.put(Constants.DOMAIN_QUERY_PARAM_REDIRECT_URI, Constants.REDIRECT_URL);
//            httpBodyParams.put(Constants.TOKEN_AUTH_TYPE_CODE, accesscode);
//
//            AuthHttpClient httpClient = new AuthHttpClient();
//            URL url = new URL(GetTokenURL());
//            String result = httpClient.httpPost(url, httpBodyParams);
//            System.out.println(result);
//
//            JSONObject payload = CognitoJWTParser.getPayload(result);
//            String provider = payload.get("iss").toString().replace("https://", "");
//            credentials = GetCredentials(provider, result);
//
//            return credentials;
//        } catch (Exception exp) {
//            System.out.println(exp);
//        }
//        return credentials;
//    }

}
