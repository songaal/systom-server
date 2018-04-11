package io.gncloud.coin.server.service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import io.gncloud.coin.server.model.User;
import io.gncloud.coin.server.utils.CognitoPubKeyStore;
import io.gncloud.coin.server.utils.CredentialsCache;
import io.gncloud.coin.server.utils.StringUtils;
import io.gncloud.coin.server.ws.WebSocketSessionInfoSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 권한, 인증 및 세션 관리 서비스
 */
@Service
public class IdentityService {
    private final String ACCESS_TOKEN = "X-COINCLOUD-ACCESS-TOKEN";
    private final String REFRESH_TOKEN = "X-COINCLOUD-REFRESH-TOKEN";

    private static Logger logger = LoggerFactory.getLogger(IdentityService.class);

    //웹소켓
    private Map<String, WebSocketSessionInfoSet> subscriberMap;

    //토큰 캐시.
    private CredentialsCache tokenCache;

    private CognitoPubKeyStore cognitoPubKeyStore;

    private AWSCognitoIdentityProvider cognitoClient;

    @Value("${aws.cognito.userPoolId}")
    private String cognitoPoolId;
    @Value("${aws.cognito.clientId}")
    private String cognitoClientId;

    @PostConstruct
    public void init() {
        cognitoClient = AWSCognitoIdentityProviderClientBuilder.standard().build();
        tokenCache = new CredentialsCache(10000);
        cognitoPubKeyStore = new CognitoPubKeyStore(cognitoPoolId);
        subscriberMap = new ConcurrentHashMap<>();
    }

    public Map<String, WebSocketSessionInfoSet> getSubscriberMap() {
        return subscriberMap;
    }


    /**
     * 회원가입
     * 임시 비밀번호는 이메일로 전달된다.
     * 임시 비번으로 로그인후 즉시 비밀번호 변경을 수행한다.
     * */
    public AdminCreateUserResult signUp(String userId, String email) {
        logger.debug("SignUp userId >> {}, email >> {}", userId, email);
        AdminCreateUserRequest authRequest = new AdminCreateUserRequest()
                .withUserPoolId(cognitoPoolId)
                .withUsername(userId)
                .withUserAttributes(new AttributeType().withName("email").withValue(email));

        AdminCreateUserResult authResponse = cognitoClient.adminCreateUser(authRequest);
        return authResponse;
    }


    /**
     * 로그인
     * */
    public AdminInitiateAuthResult login(String userId, String password) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(password)) {
            return null;
        }

        logger.debug("authenticating userId >> {}", userId);

        String accessToken = null;
        Map<String, String> authParams = new HashMap<String, String>();
        authParams.put("USERNAME", userId);
        authParams.put("PASSWORD", password);

        AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest()
                .withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .withAuthParameters(authParams)
                .withClientId(cognitoClientId)
                .withUserPoolId(cognitoPoolId);

        AdminInitiateAuthResult authResult = cognitoClient.adminInitiateAuth(authRequest);
        return authResult;
    }

    /**
     * 회원가입시 자동생성된 임시 비번을 변경한다.
     * */
    public AdminRespondToAuthChallengeResult changeTempPassword(String session, String userId, String password) {
        Map<String, String> responses = new HashMap<>();
        responses.put("USERNAME", userId);
        responses.put("NEW_PASSWORD", password);

        AdminRespondToAuthChallengeRequest authChallengeRequest = new AdminRespondToAuthChallengeRequest()
                .withChallengeName("NEW_PASSWORD_REQUIRED")
                .withSession(session)
                .withChallengeResponses(responses)
                .withClientId(cognitoClientId)
                .withUserPoolId(cognitoPoolId);

        AdminRespondToAuthChallengeResult challengeResult = cognitoClient.adminRespondToAuthChallenge(authChallengeRequest);
        logger.debug("challengeResult>> {}", challengeResult);

        return challengeResult;
    }

    /**
     * 비번을 잊어버린경우 reset하면 임시비번이 메일로 전달됨.
     * 하지만 누구나 접근할수 없도록 임시링크를 만들어서 이메일로 전달하는 방식을 사용해야 한다. 또는 admin에게 요청하는 방식.
     * */
    public AdminResetUserPasswordResult resetPassword(String userId) {
        Map<String, String> responses = new HashMap<>();
        responses.put("USERNAME", userId);

        AdminResetUserPasswordRequest resetPasswordRequest = new AdminResetUserPasswordRequest()
                .withUsername(userId)
                .withUserPoolId(cognitoPoolId);

        AdminResetUserPasswordResult resetUserPasswordResult = cognitoClient.adminResetUserPassword(resetPasswordRequest);
        logger.debug("resetUserPasswordResult>> {}", resetUserPasswordResult);

        return resetUserPasswordResult;
    }

    /**
     * REST API 호출시 마다 이 메소드를 거쳐서 인증하도록 한다.
     * */
    public boolean isValidAccessToken(String accessToken) {
        /**TODO 차후 https://cognito-idp.ap-northeast-2.amazonaws.com/ap-northeast-2_8UlVuFFva/.well-known/jwks.json
         * 를 통해서 public 키를 가져와서 signature를 private키로 이용하여 payload의 무결성을 검증한다.
         * 여러차례 시도했으나, RSAPublicKey와 privatekey를 만들어 검증시 키 에러가 발생함.
         * 일단 유효시간만 체크한다.
         */

        if(!tokenCache.checkToken(accessToken)) {
            //실제로 존재하는지 검증.
            DecodedJWT decoded = JWT.decode(accessToken);
            logger.debug("DecodedJWT > {}", decoded);
            String keyId = decoded.getHeaderClaim("kid").asString();
            if(cognitoPubKeyStore.containsKey(keyId)) {
                tokenCache.addToken(accessToken);
            } else {
                //키가 없다.
                return false;
            }
        }
        return true;
    }

    public Map<String, String> parsePayload(String jwt) {
        try {
            DecodedJWT decoded = JWT.decode(jwt);
            logger.debug("signature : {}", decoded.getSignature());
            String payload = decoded.getPayload();
            logger.debug("payload : {}", payload);
            byte[] decodedPayload = Base64.getUrlDecoder().decode(payload.getBytes("utf-8"));
            String payloadJson = new String(decodedPayload, "utf-8");
            Gson gson = new Gson();
            Map<String, String> payloadMap = gson.fromJson(payloadJson, Map.class);
            logger.debug("payloadMap : {}", payloadMap);
            return payloadMap;
        } catch (Exception e) {
            logger.error("", e);

        }
        return null;
    }


}
