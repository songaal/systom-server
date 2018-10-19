package io.systom.coin;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public class BillingRestApiTest {

    private String accessToken;
    private int expiredAt;

    @Before
    public void init() {
        /*
         * 액세스키를 얻는다.
         * */
//        String 가맹점식별코드 = "imp52219493";
        String impKey = "7173788573360317";
        String impSecret = "uuvKahDJzr2VEnIp3acBpwGzJ0tufZTJuotFF7pfzradPTDT4Pvtmb25cwzrwsa0REFxL4MkwXymQbfU";
        String url = "https://api.iamport.kr/users/getToken";
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> params= new LinkedMultiValueMap<>();
        params.add("imp_key", impKey);
        params.add("imp_secret", impSecret);
        HttpEntity entity = new HttpEntity(params, headers);
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        boolean isSuccess = responseEntity.getStatusCode().is2xxSuccessful();
        Map body = responseEntity.getBody();
        int code = (int) body.get("code");
        if(isSuccess && code == 0) {
            System.out.println("인증성공!");
            Map response = (Map) body.get("response");
            accessToken = (String) response.get("access_token");
            expiredAt = (int) response.get("expired_at");
            System.out.println("Status : " + responseEntity.getStatusCode());
            System.out.println("accessToken : " + accessToken);
            System.out.println("expiredAt : " + expiredAt);
        } else {
            System.out.println("인증에러!");
        }
    }


    /**
     *
     * 신용카드를 등록한다.
     *
     * 결과예시 : <200,{code=0, message=null, response={card_code=361, card_name=BC카드, customer_addr=null, customer_email=null, customer_name=null, customer_postcode=null, customer_tel=null, customer_uid=swsong_3497, inserted=1539844328, updated=1539844328}},{Content-Type=[application/json; charset=UTF-8], Date=[Thu, 18 Oct 2018 06:32:07 GMT], Server=[Apache/2.4.7 (Ubuntu)], Content-Length=[266], Connection=[keep-alive]}>
     * */
    @Test
    public void testIssueBilling() {

        /* 카드정보 */
        /*-------------------------------*/
        String cardNumber = "5389200013543497"; //카드번호
        String expiry = "202105"; //유효기간 YYYYMMDD
        String birthOrCorpNumber = "781231"; //생년월일6자리 또는 사업자번호 10자리
        String password2digit = "58"; // 비밀번호 앞2자리
        String userId = "swsong"; //회원아이디
        /*-------------------------------*/
        String cardLast4Number = cardNumber.substring(cardNumber.length() - 4);
        /* 여기서 전달하는 customerUid가 앞으로 결제요청시 계속 사용하게 되는 사용자키다.*/
        String customerUid = userId + "_" + cardLast4Number;
        System.out.println("customerUid : " + customerUid);

        String url = "https://api.iamport.kr/subscribe/customers/" + customerUid;
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", accessToken);
        MultiValueMap<String, String> params= new LinkedMultiValueMap<>();
        params.add("card_number", cardNumber);
        params.add("expiry", expiry);
        params.add("birth", birthOrCorpNumber);
        params.add("pwd_2digit", password2digit);
        HttpEntity entity = new HttpEntity(params, headers);
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        System.out.println(responseEntity);
        Map body = responseEntity.getBody();
        int code = (int) body.get("code");
        String message = (String) body.get("message");
        if (code == 0) {
            System.out.println("발급성공!");
        }
    }

    @Test
    public void testPay() {

        String customerUid = "swsong_3497"; //카드등록시 설정한 UID
        String merchantUid = "systom-" + System.nanoTime(); //날짜와 순번으로 조합해서 만든다.
        String amount = "100";
        String description = "유료플랜 1000";

        String url = "https://api.iamport.kr/subscribe/payments/again";
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", accessToken);
        MultiValueMap<String, String> params= new LinkedMultiValueMap<>();
        params.add("customer_uid", customerUid);
        params.add("merchant_uid", merchantUid);
        params.add("amount", amount);
        params.add("name", description);
        HttpEntity entity = new HttpEntity(params, headers);
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        System.out.println(responseEntity);
        Map body = responseEntity.getBody();
        int code = (int) body.get("code");
        String message = (String) body.get("message");
        Map response = (Map) body.get("response");
        String status = (String) response.get("status");
        if (code == 0 && status.equals("paid")) {
            System.out.println("결제성공!");
        } else {
            System.out.println("결제실패!");
        }
    }

    /**
     * 다음번 결제를 예약한다.
     * 1회성 예약이며 다다음달로 자동갱신되지는 않는다.
     */
    @Test
    public void testScheduleNextPay() {

        String customerUid = "swsong_3497"; //카드등록시 설정한 UID
        String merchantUid = "systom-" + System.nanoTime(); //날짜와 순번으로 조합해서 만든다.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 1); // 1분뒤 예약.
//        cal.set(2018, 11-1, 18, 9, 30);
        System.out.println(cal);
        String scheduleAt = String.valueOf(cal.getTimeInMillis() / 1000);
        String amount = "100";
        String description = "유료플랜 1000 11월 정기결제";

        String url = "https://api.iamport.kr/subscribe/payments/schedule";
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HashMap<String, Object> params= new HashMap<>();
        params.put("customer_uid", customerUid);

        List schedules = new ArrayList();
        Map<String, String> schedule= new HashMap<>();
        schedule.put("merchant_uid", merchantUid);
        schedule.put("schedule_at", scheduleAt); //timestamp 초단위.
        schedule.put("amount", amount);
        schedule.put("name", description);
        schedules.add(schedule);
        params.put("schedules", schedules);
        String json = new Gson().toJson(params);
        System.out.println("json >> " + json);
        HttpEntity entity = new HttpEntity(json, headers);
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        System.out.println(responseEntity);
        Map body = responseEntity.getBody();
        System.out.println("body >> " + body);
        int code = (int) body.get("code");
        String message = (String) body.get("message");
        if (code == 0) {
            System.out.println("스케줄 성공!");
        } else {
            System.out.println("스케줄 실패!");
        }
    }

    @Test
    public void testCheckResult() {
        String impUid = "imps_370951057727"; //결제 완료시 마다 만들어지는 ID
        String url = "https://api.iamport.kr/payments/" + impUid;
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", accessToken);
//        headers.setContentType(MediaType.APPLICATION_JSON);
        HashMap<String, Object> params= new HashMap<>();
        HttpEntity entity = new HttpEntity(null, headers);
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        System.out.println(responseEntity);
        // 결과 json을 결과 DB에 넣고 필요한 정보는 파싱해서 쓴다.
    }

    @Test
    public void testCancel() {
        String impUid = "imps_370951057727"; //결제 완료시 마다 만들어지는 ID
        String url = "https://api.iamport.kr/payments/cancel";
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", accessToken);
        HashMap<String, Object> params= new HashMap<>();
        params.put("imp_uid", impUid);
        HttpEntity entity = new HttpEntity(params, headers);
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        System.out.println(responseEntity);
        // 결과 json을 결과 DB에 넣고 필요한 정보는 파싱해서 쓴다.
    }

}
