package io.gncloud.coin.aws;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import io.gncloud.coin.server.utils.RestUtils;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AwsCognitoTest {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(AwsCognitoTest.class);

    AWSCognitoIdentityProvider cognitoClient;


    @Test
    public void cognitoSignUpTest() {

        cognitoClient = AWSCognitoIdentityProviderClientBuilder.standard().build();

        String userId = "songaal4";
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
            DecodedJWT decoded = JWT.decode(accessToken);
            logger.debug("DecodedJWT > {}", decoded);
        }


    }

    @Test
    public void testJwtSignature() throws UnsupportedEncodingException {
        String jwt = "eyJraWQiOiJGcm9lZEZMNXBkaWlvMlh5emhsXC9xT3h2RmpUcWVPUzRNOTNpSFBhSjFEcz0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxNThkOTQ3NC01YmNkLTQwYTItYmFmNS1jN2YwNjNhNmU5NTEiLCJldmVudF9pZCI6IjA4MjQ1Yzc2LTNjNTQtMTFlOC04YWEzLTNkYTJkMDQzYmQ4YiIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiYXdzLmNvZ25pdG8uc2lnbmluLnVzZXIuYWRtaW4iLCJhdXRoX3RpbWUiOjE1MjMzMTkyMjQsImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC5hcC1ub3J0aGVhc3QtMi5hbWF6b25hd3MuY29tXC9hcC1ub3J0aGVhc3QtMl84VWxWdUZGdmEiLCJleHAiOjE1MjMzMjI4MjQsImlhdCI6MTUyMzMxOTIyNCwianRpIjoiMDY4NDdhOTEtZGNjZC00NDhhLTg2Y2EtOTg3ZmVmZGJlNDMzIiwiY2xpZW50X2lkIjoiNGttODNqYnQxZDZwZzQxNXE0aWVxdDQxYjAiLCJ1c2VybmFtZSI6InNvbmdhYWw0In0.IDOOhlgkRnz05IKTKuDW5ZDhH_dwoFCawKZ0QRDbI89vpmpyFeaHdAuh4ZCzynnRveI96O1G7qmxjBVGVpmD8xNtz6XGI0R4d32sCcUFuo0xlhV58mvGiLPDz8-Z0fcScXtp2mJYL5j1Ng0BqlHyk8a9K7cVACfSd4fdK_qIfW16Z8HUIhOLDTWX9bgWJGAdr1mD2ekmIJf6KJ5a44YWkgguxvyBzz4yK4J4pmocp8OyqMp9u7LXSCPqrO-x1VK1FmBDpK5uXwPcCM9m9YeWtD_gkI8Nitt8s2XYrn2hTTeD21DYYLzGYOF9mjAfZnANdO7Oemc4n_G5OG_dC1TW4g";
        DecodedJWT decoded = JWT.decode(jwt);
        logger.debug("signature : {}", decoded.getSignature());
        String payload = decoded.getPayload();
        logger.debug("payload : {}", payload);
        byte[] decodedPayload = Base64.getUrlDecoder().decode(payload.getBytes("utf-8"));
        String payloadJson = new String(decodedPayload, "utf-8");
        Gson gson = new Gson();
        Map<String, String> payloadMap = gson.fromJson(payloadJson, Map.class);
        logger.debug("payloadMap : {}", payloadMap);
    }


    @Test
    public void testPubKey(){

        String userPoolId = "ap-northeast-2_8UlVuFFva";
        String region = userPoolId.split("_")[0];
        String url = String.format("https://cognito-idp.%s.amazonaws.com/%s/.well-known/jwks.json", region, userPoolId);

        Map<String, Object> map = RestUtils.getObject(url, Map.class);
        System.out.println(map);

        List list = (List) map.get("keys");

        if(list != null) {
            Map keyMap = new HashMap<String, Map>();
            for(Object km : list) {
                keyMap.put(((Map)km).get("kid"), km);
            }


            System.out.println(keyMap);
        }

    }

    /**
     * https://docs.aws.amazon.com/ko_kr/cognito/latest/developerguide/user-pool-settings-email-phone-verification.html
     *
     * 사용자 토큰이 필요하므로 로그인된 상태에서 사용해야함. 모달창으로 코드를 입력받는 등의 ux가 필요함.
     *
     * */
    public void testVerifyCodeTest() {
        String accessToken = "";
        //이메일로 확인코드를 보낸다.
        GetUserAttributeVerificationCodeRequest request = new GetUserAttributeVerificationCodeRequest()
                .withAccessToken(accessToken)
                .withAttributeName("email"); //이메일 고정
        cognitoClient.getUserAttributeVerificationCode(request);

        String code = "<이메일로 전달된 코드>";
        //받은 code를 보내서 통과확인한다.
        VerifyUserAttributeRequest verifyUserRequest = new VerifyUserAttributeRequest()
                .withAccessToken(accessToken)
                .withAttributeName("email")
                .withCode(code);
        cognitoClient.verifyUserAttribute(verifyUserRequest);

    }

    @Test
    public void refreshToken() {
        String refreshToken = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiUlNBLU9BRVAifQ.qfjLvVORLarriqVX0XAVVn81l-MyRwKAGfhEYp-oSWUEuC7YyPBRLgZqmyjhoqAumsPSmUs18wECwY3ZFN3BxYHv0KiB0HhJmGZeHM2a6prbEorp9Pj1xhsy-Sclaiw0n5uLg2B6mXRdii4sMo8Grl9rnEZD5ZNF2Zcb7oIRXIssfnmoz0s_C5kG0v9j8Hh-sKrAgfzxgzzvLtOBnLm5Jyymla3nJE62uDmbTZqQ5-7UI2jHPpgmWoxr9DZ3YdDEOKG-c-CZMlmgSpdkdCqjJbzH5FL1DATRfiExbXerf8D99qZWgHdjHexzeoGcBHBsXHcUXJHdlOXp0zWvBMXmdw.NyhDObUmSuyi26UL.Q5eOjlRM2NAv-S2GHWdW29xc8m4aSB2RvkfcKri_jzn_DLq60NdN0gfS4TD4OkX0Zzh9z85U4Vp6eRzHAbxCbGUByXAf2P0fxruLkOf3GS3CzRV4YlzyA3j7RgofYY-AuFCGXKyOBLHioThY4GXOIW1_Z8Jmvr4zMb7lrlMTWlZlS1r7AkzVSh5-6hGcg1_SqZ-WAVQdIZUPr2SrOoX99MYJvj23vS3o5hlllJizAsZXSKsApNLR462NVSUb00QwRn2FQuS2E31h8jOxIgfZ24R9dVAFtfzBzmTznTzFXTPYE_FB-H0FLdv7pglGGO0lQZllxwMceriPPB84XxXZlz2db3con-IHYknGr3E2FoPoh0mGw1ETBgXw7ZtDczaxCI8Sx-qA41cchpibfYCHR1PbdmKfZoeDTtXTqHmUvyeEqNUraHu7Oc2X-MXEEZg-N2GFqk5FPnYCpKee31AY3_MCFTAapdz1o_4Yi9ZBdsHN_FmhwNbX5Jcewoibh_7BdKfDXK66sjOoYvZ8jDLuYk5Rl64zWfig4JjTjZLVZTwtnmxgm6RBCUtxY4gXi7XaBXx7YkTkoLIi0S183sUnPrKc3jLiDO-c6fjLnBaefSbnqcxxTdOm-BtPyMy92tFNIDSUj3jHiXSX1S2MGtX_wik8JEt5WZ2MQMmRHE2vZxuoUtFuadbh-HitJCaboLgKiMFyopp_vd-8qdmtMLHkIVywbKT_rZnZ8qtMRH9sC0LYuLFgv4ax40rAzaPvdMCWIz8_mFFA-9ilyjecF1XKPPgsJSzP-LJA8knpacltJ-JTwk7IEueTZZLUe96-SmgwCd4PbKfaCNoDz--2hTpu2WvyhLFEbSaUf-qJDuvzhRFj0KoKD5QzX7HFksTdeILe6mdU3LKCck9fv-h2TmymHpxX9y1l-2F64lschJLKfG8VKssVJjtarhCVsmdPUvFUX42rOiMU8smovRwC7K2PYbXKfA4nicpfYdm4M6EqnB2_w2hczRZ1tePgvjwy3ZYviL95hUdakUr7FEJKNVAB6gw0Bw-K8BkGL065tUQXAXFfskEP9AMihQNk9E0p9fPevGRwbytVDlmKjpsBXxPSJ8mEwoqlkDBUTakw4lFD-Knj3xpEsuxQ9rDSElv_uogaBXBbDb_5lHW3RicLy2B_h_mNdlG7adBSmWDb4pBSILBqmPMdv2W7aA8VhfexrVMSjKjIcKQGdzves6mtY9RxF7J-L9UkWtxnJg3JHjTNDeY6ZtI9ldsYqJkahk23OVNlL7k4vfiEHD9F2hvWpcJ0Bqw33g.L5HtAMqn9FGSJ8WXYkKHFA";
        logger.debug("refresh token>> {}", refreshToken);

        Map<String, String> authParams = new HashMap<String, String>();
        authParams.put("REFRESH_TOKEN", refreshToken);

        AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest()
                .withAuthFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
                .withAuthParameters(authParams)
                .withClientId("4km83jbt1d6pg415q4ieqt41b0")
                .withUserPoolId("ap-northeast-2_8UlVuFFva");
        cognitoClient = AWSCognitoIdentityProviderClientBuilder.standard().build();
        AdminInitiateAuthResult AdminInitiateAuthResult = cognitoClient.adminInitiateAuth(authRequest);

        AuthenticationResultType AuthenticationResultType = AdminInitiateAuthResult.getAuthenticationResult();

        logger.debug("old RefreshToken : {}", refreshToken);
        logger.debug("newAccessToken : {}", AuthenticationResultType.getAccessToken());
        logger.debug("ExpiresIn : {}", AuthenticationResultType.getExpiresIn());
        logger.debug("RefreshToken : {}", AuthenticationResultType.getRefreshToken());
        logger.debug("TokenType : {}", AuthenticationResultType.getTokenType());


        AuthenticationResultType = AdminInitiateAuthResult.getAuthenticationResult();

        logger.debug("old RefreshToken : {}", refreshToken);
        logger.debug("newAccessToken : {}", AuthenticationResultType.getAccessToken());
        logger.debug("ExpiresIn : {}", AuthenticationResultType.getExpiresIn());
        logger.debug("RefreshToken : {}", AuthenticationResultType.getRefreshToken());
        logger.debug("TokenType : {}", AuthenticationResultType.getTokenType());
    }

    @Test
    public void resetPassword(){

        AdminResetUserPasswordRequest resetPasswordRequest = new AdminResetUserPasswordRequest()
                .withUsername("joonwoo")
                .withUserPoolId("ap-northeast-2_8UlVuFFva");
        cognitoClient = AWSCognitoIdentityProviderClientBuilder.standard().build();

        AdminResetUserPasswordResult resetUserPasswordResult = cognitoClient.adminResetUserPassword(resetPasswordRequest);
        logger.debug("resetUserPasswordResult>> {}", resetUserPasswordResult);

    }
}