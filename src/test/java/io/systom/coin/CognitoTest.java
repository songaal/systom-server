package io.systom.coin;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;
import io.systom.coin.utils.CognitoPubKeyStore;
import io.systom.coin.utils.CredentialsCache;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/*
 * create joonwoo 2018. 7. 27.
 * 
 */
public class CognitoTest {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(CognitoTest.class);
    private CredentialsCache tokenCache;
    private CognitoPubKeyStore cognitoPubKeyStore;
    private AWSCognitoIdentityProvider cognitoClient;

    @Before
    public void init() {
        cognitoClient = AWSCognitoIdentityProviderClientBuilder.standard().build();
        tokenCache = new CredentialsCache(10000);
        cognitoPubKeyStore = new CognitoPubKeyStore("ap-northeast-2_8UlVuFFva");
    }


    @Test
    public void userList() {
        String email = "email";

        ListUsersRequest listUsersRequest = new ListUsersRequest();
        listUsersRequest.withUserPoolId("ap-northeast-2_8UlVuFFva");
        ListUsersResult listUsersResult = cognitoClient.listUsers(listUsersRequest);
        Iterator<UserType> userIterator = listUsersResult.getUsers().iterator();
        while(userIterator.hasNext()){
            UserType userType = userIterator.next();
            Iterator<AttributeType> attributeTypeIterator = userType.getAttributes().iterator();
            while(attributeTypeIterator.hasNext()) {
                AttributeType attributeType = attributeTypeIterator.next();
                if (attributeType.getName().equalsIgnoreCase(email.toUpperCase())) {
                    logger.info("동일 이메일 발견.");
                }
            }
        }
        logger.info("user list: {}", listUsersResult);
    }

    @Test
    public void signupTest() {
        AdminCreateUserRequest authRequest = new AdminCreateUserRequest()
                .withUserPoolId("ap-northeast-2_8UlVuFFva")
                .withUsername("testuser")
                .withUserAttributes(new AttributeType().withName("email").withValue("jwkim@gncloud.kr"))
                .withUserAttributes(new AttributeType().withName("email_verified").withValue("true"));

        AdminCreateUserResult authResponse = cognitoClient.adminCreateUser(authRequest);
        logger.info("{}", authResponse);
    }


    @Test
    public void resetPasswordTest() {
        String userId = "test1222";

        AnonymousAWSCredentials awsCreds = new AnonymousAWSCredentials();
        AWSCognitoIdentityProvider cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(Regions.AP_NORTHEAST_2)
                .build();

        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
        forgotPasswordRequest.setUsername(userId);
        forgotPasswordRequest.setClientId("4km83jbt1d6pg415q4ieqt41b0");
        ForgotPasswordResult forgotPasswordResult = null;

        try {
            forgotPasswordResult = cognitoIdentityProvider.forgotPassword(forgotPasswordRequest);
            String sendMail = forgotPasswordResult.getCodeDeliveryDetails().getDestination();
            logger.info("{}, {}", sendMail, forgotPasswordResult);

//            UpdatePassword(userId, "test1111", "028221");
        } catch (Exception e) {
            // handle exception here
            logger.error("", e);
        }

        logger.info("result: {}", forgotPasswordResult.toString());
    }

    @Test
    public void UpdatePassword() {
        String username = "test1";
        String newpw = "test1111";
        String code = "597101";
        AnonymousAWSCredentials awsCreds = new AnonymousAWSCredentials();
        AWSCognitoIdentityProvider cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(Regions.AP_NORTHEAST_2)
                .build();

        ConfirmForgotPasswordRequest confirmPasswordRequest = new ConfirmForgotPasswordRequest();
        confirmPasswordRequest.setUsername(username);
        confirmPasswordRequest.setPassword(newpw);
        confirmPasswordRequest.setConfirmationCode(code);
        confirmPasswordRequest.setClientId("4km83jbt1d6pg415q4ieqt41b0");
        ConfirmForgotPasswordResult confirmPasswordResult = new ConfirmForgotPasswordResult();

        try {
            confirmPasswordResult = cognitoIdentityProvider.confirmForgotPassword(confirmPasswordRequest);
        } catch (Exception e) {
            // handle exception here
        }
        logger.info("{}", confirmPasswordResult.toString());
    }


    @Test
    public void disableUser() {
        String userId = "test1";

        AdminDisableUserRequest adminDisableUserRequest = new AdminDisableUserRequest();
        adminDisableUserRequest.withUsername(userId).withUserPoolId("ap-northeast-2_8UlVuFFva");

        AdminDisableUserResult adminDisableUserResult = cognitoClient.adminDisableUser(adminDisableUserRequest);
        logger.info("{}", adminDisableUserResult);
    }


    @Test
    public void changePassword () {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.withAccessToken("eyJraWQiOiJGcm9lZEZMNXBkaWlvMlh5emhsXC9xT3h2RmpUcWVPUzRNOTNpSFBhSjFEcz0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIyMjliNWU3Yy1mNTk1LTQ4ZmYtYmYwMC1lNzNiODU0NWMyOGYiLCJldmVudF9pZCI6ImQ5Mzc1MzQ5LTkxNzYtMTFlOC04ZGJlLWRmNDJmYjQ4NGRjYiIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiYXdzLmNvZ25pdG8uc2lnbmluLnVzZXIuYWRtaW4iLCJhdXRoX3RpbWUiOjE1MzI2ODAwMjcsImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC5hcC1ub3J0aGVhc3QtMi5hbWF6b25hd3MuY29tXC9hcC1ub3J0aGVhc3QtMl84VWxWdUZGdmEiLCJleHAiOjE1MzI2ODM2MjcsImlhdCI6MTUzMjY4MDAyNywianRpIjoiMGE3ZGQ1OGMtMzNjMC00NDk0LWE0MjYtZjQwYjhkNGY3OGU1IiwiY2xpZW50X2lkIjoiNGttODNqYnQxZDZwZzQxNXE0aWVxdDQxYjAiLCJ1c2VybmFtZSI6InRlc3QxIn0.ErbJuZ1pbD75JG9-w9V9rYSrwpn991TO46x_9lAlXb_DvJmygt2QC4xLqbIcylt_fdV7wx3rJrllMK_SmKFWEDGfqAzC7FRCDMGEAggbVCSdjHVTt5fi1nAUxsLgRoXg8yd0MEyCUV4uZ0HQHH_bOY1vDOiypAijewjgheg4nr9mMyX9f73xXFIhxBXN3ZI3gwSeeRdiqtX9QWtl0m0PNtlYIPsqykPCgVrUZFxy__MYCs5bImEJrv7yulfFBVyrRKVBdgoa-84hWyLDMoFpW2elUA7fbH_nzE11tNSnBZbtmNXv-9FDLOYtd0BOoaESApmODgpCPAuMJKDxQSveKw")
                .withPreviousPassword("test1111")
                .withProposedPassword("test1234");
        ChangePasswordResult changePasswordResult = cognitoClient.changePassword(changePasswordRequest);
        logger.info("{}", changePasswordResult);
    }


    @Test
    public void addAttrTest () {
        ListUsersRequest listUsersRequest = new ListUsersRequest();
        listUsersRequest.withUserPoolId("ap-northeast-2_8UlVuFFva");
        ListUsersResult listUsersResult = cognitoClient.listUsers(listUsersRequest);
        Iterator<UserType> userIterator = listUsersResult.getUsers().iterator();
        while(userIterator.hasNext()){
            UserType userType = userIterator.next();
            logger.info("{}", userType.getAttributes());
            userType.getAttributes().add(new AttributeType().withName("maxInvitation").withValue("5"));
            AdminUpdateUserAttributesRequest userAttributesRequest = new AdminUpdateUserAttributesRequest();
            userAttributesRequest.withUserPoolId("ap-northeast-2_8UlVuFFva")
                    .withUsername(userType.getUsername())
                    .withUserAttributes(userType.getAttributes());
            cognitoClient.adminUpdateUserAttributes(userAttributesRequest);
        }
    }
}