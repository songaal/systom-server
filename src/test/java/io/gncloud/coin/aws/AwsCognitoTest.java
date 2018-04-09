package io.gncloud.coin.aws;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;
import com.amazonaws.util.Base64;
import com.amazonaws.util.StringUtils;
import io.gncloud.coin.server.utils.CognitoJWTParser;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class AwsCognitoTest {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(AwsCognitoTest.class);

    AWSCognitoIdentityProvider cognitoClient;


    @Test
    public void cognitoSignUpTest() {

        cognitoClient = AWSCognitoIdentityProviderClientBuilder.standard().build();

        String userId = "songaal4";
        String password = "123123";
        String email = "songaal@gmail.com";

        AdminCreateUserRequest authRequest = new AdminCreateUserRequest()
                .withUserPoolId("ap-northeast-2_8UlVuFFva")
                .withUsername(userId)
                .withUserAttributes(new AttributeType().withName("email").withValue(email));

        AdminCreateUserResult authResponse = cognitoClient.adminCreateUser(authRequest);
        logger.info("authResponse>> {}", authResponse);

    }


    @Test
    public void cognitoLoginTest() {

        String accessToken = null;

        cognitoClient = AWSCognitoIdentityProviderClientBuilder.standard().build();

        String userId = "songaal4";
        String password = "123123";
        String email = "songaal@gmail.com";
        String cognitoClientId = "4km83jbt1d6pg415q4ieqt41b0";
        String userPoolId = "ap-northeast-2_8UlVuFFva";
        Map<String, String> authParams = new HashMap<>();
        authParams.put("USERNAME", userId);
        authParams.put("PASSWORD", password);

        AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest()
                .withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .withAuthParameters(authParams)
                .withClientId(cognitoClientId)
                .withUserPoolId(userPoolId);

        AdminInitiateAuthResult authResponse = cognitoClient.adminInitiateAuth(authRequest);

        String session = authResponse.getSession();
        String challengeName = authResponse.getChallengeName();
        AuthenticationResultType authResult = authResponse.getAuthenticationResult();

        logger.info("session>> {}", session);
        logger.info("authResult>> {}, authResult");


        if(challengeName != null) {

            Map<String, String> challengeParams = authResponse.getChallengeParameters();
            logger.info("challengeName>> {} >> {}", challengeName, challengeParams);

            if (challengeName.equals("NEW_PASSWORD_REQUIRED")) {
                Map<String, String> responses = new HashMap<>();
                /* USERNAME required always */
                responses.put("USERNAME", userId);
                responses.put("NEW_PASSWORD", "123123");
                responses.put("EMAIL", email);

                AdminRespondToAuthChallengeRequest authChallengeRequest = new AdminRespondToAuthChallengeRequest()
                        .withChallengeName(challengeName)
                        .withSession(session)
                        .withChallengeResponses(responses)
                        .withClientId(cognitoClientId)
                        .withUserPoolId(userPoolId);

                AdminRespondToAuthChallengeResult challengeResult = cognitoClient.adminRespondToAuthChallenge(authChallengeRequest);
                logger.info("challengeResult>> {}", challengeResult);

                authResult = challengeResult.getAuthenticationResult();
            }
        }


        if(authResult != null) {
            accessToken = authResult.getAccessToken();
            JSONObject header = CognitoJWTParser.getHeader(accessToken);
            JSONObject payload = CognitoJWTParser.getPayload(accessToken);
            logger.debug("header > {}", header);
            logger.debug("payload > {}", payload);
        }


    }




}