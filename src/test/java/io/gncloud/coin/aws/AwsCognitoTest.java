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

import java.nio.charset.StandardCharsets;
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
        logger.info("authResult>> {}", authResult);


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
            String signature = CognitoJWTParser.getSignature(accessToken);
            logger.debug("jwt > {}", accessToken);
            logger.debug("header > {}", header);
            logger.debug("payload > {}", payload);
            logger.debug("signature > {}", signature);
        }


    }


    @Test
    public void testJwtSignature() {
        String jwt = "eyJraWQiOiJGcm9lZEZMNXBkaWlvMlh5emhsXC9xT3h2RmpUcWVPUzRNOTNpSFBhSjFEcz0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxNThkOTQ3NC01YmNkLTQwYTItYmFmNS1jN2YwNjNhNmU5NTEiLCJldmVudF9pZCI6IjA4MjQ1Yzc2LTNjNTQtMTFlOC04YWEzLTNkYTJkMDQzYmQ4YiIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiYXdzLmNvZ25pdG8uc2lnbmluLnVzZXIuYWRtaW4iLCJhdXRoX3RpbWUiOjE1MjMzMTkyMjQsImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC5hcC1ub3J0aGVhc3QtMi5hbWF6b25hd3MuY29tXC9hcC1ub3J0aGVhc3QtMl84VWxWdUZGdmEiLCJleHAiOjE1MjMzMjI4MjQsImlhdCI6MTUyMzMxOTIyNCwianRpIjoiMDY4NDdhOTEtZGNjZC00NDhhLTg2Y2EtOTg3ZmVmZGJlNDMzIiwiY2xpZW50X2lkIjoiNGttODNqYnQxZDZwZzQxNXE0aWVxdDQxYjAiLCJ1c2VybmFtZSI6InNvbmdhYWw0In0.IDOOhlgkRnz05IKTKuDW5ZDhH_dwoFCawKZ0QRDbI89vpmpyFeaHdAuh4ZCzynnRveI96O1G7qmxjBVGVpmD8xNtz6XGI0R4d32sCcUFuo0xlhV58mvGiLPDz8-Z0fcScXtp2mJYL5j1Ng0BqlHyk8a9K7cVACfSd4fdK_qIfW16Z8HUIhOLDTWX9bgWJGAdr1mD2ekmIJf6KJ5a44YWkgguxvyBzz4yK4J4pmocp8OyqMp9u7LXSCPqrO-x1VK1FmBDpK5uXwPcCM9m9YeWtD_gkI8Nitt8s2XYrn2hTTeD21DYYLzGYOF9mjAfZnANdO7Oemc4n_G5OG_dC1TW4g";
        byte[] decodedImg = Base64.getDecoder().decode(encodedImg.getBytes(StandardCharsets.UTF_8));


        java.util.Base64.Decoder dec= java.util.Base64.getDecoder();
        String encodedSignature = jwt.split("\\.")[2];
        logger.debug("encodedSignature : {}", encodedSignature);
        final byte[] sectionDecoded = dec.decode(encodedSignature.getBytes(StandardCharsets.UTF_8));

        logger.debug("sectionDecoded : {}", sectionDecoded);

    }




}