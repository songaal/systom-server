package io.systom.coin.service;

import com.google.gson.Gson;
import io.systom.coin.model.Card;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class BillingService {

    private static Logger logger = LoggerFactory.getLogger(BillingService.class);

    public String getAccessToken(String impKey, String impSecret) {
        /*
         * 액세스키를 얻는다.
         * */
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
            logger.debug("인증성공!");
            Map response = (Map) body.get("response");
            String accessToken = (String) response.get("access_token");
            logger.debug("Status : {}", responseEntity.getStatusCode());
            logger.debug("accessToken : {}", accessToken);
            return accessToken;
        } else {
            logger.error("인증에러!");
        }
        return null;
    }

    /**
     * @param accessToken
     * @param card
     */
    public void issueBilling(String accessToken, Card card) {
        /* 카드정보 */
        /*-------------------------------*/
        String cardNumber = card.getCardNo(); //카드번호
        String expiry = card.getYear() + card.getMonth(); //유효기간 YYYYMMDD
        String birthOrCorpNumber = card.getBirthDate(); //생년월일6자리 또는 사업자번호 10자리
        String password2digit = card.getPassword2(); // 비밀번호 앞2자리
        String userId = card.getUserId(); //회원아이디
        /*-------------------------------*/
        String cardLast4Number = cardNumber.substring(cardNumber.length() - 4);
        /* 여기서 전달하는 customerUid가 앞으로 결제요청시 계속 사용하게 되는 사용자키다.*/
        String customerUid = userId + "_" + cardLast4Number;
        logger.debug("customerUid : " + customerUid);

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
        logger.debug("responseEntity >> {}", responseEntity);
        Map body = responseEntity.getBody();
        int code = (int) body.get("code");
        String message = (String) body.get("message");
        if (code == 0) {
            logger.debug("카드등록 성공!");
        } else {
            logger.debug("카드등록 실패!");
        }
    }


    public void deleteBilling() {
        //TODO 아이엠포트의 빌링키 지우기.





    }




    /**
     * @param accessToken 액세스토큰
     * @param customerUid 카드등록시 설정한 UID
     * @param merchantUid 날짜와 순번으로 조합해서 만든다. 결제시 마다 매번 달라야함.
     * @param amount      금액.KRW
     * @param description 상품명
     */
    public void makePay(String accessToken, String customerUid, String merchantUid, String amount, String description) {
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
        logger.debug("responseEntity >> {}", responseEntity);
        Map body = responseEntity.getBody();
        int code = (int) body.get("code");
        String message = (String) body.get("message");
        Map response = (Map) body.get("response");
        String status = (String) response.get("status");
        if (code == 0 && status.equals("paid")) {
            logger.debug("결제성공!");
        } else {
            logger.debug("결제실패!");
        }
    }

    public void scheduleNextPay(String accessToken, String customerUid, String merchantUid, String amount, String description, Calendar scheduleCal) {

        logger.debug("scheduleCal >> {}", scheduleCal.getTime());
        String scheduleAt = String.valueOf(scheduleCal.getTimeInMillis() / 1000);

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
        logger.debug("json >> {}", json);
        HttpEntity entity = new HttpEntity(json, headers);
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        logger.debug("responseEntity >> {}", responseEntity);
        Map body = responseEntity.getBody();
        int code = (int) body.get("code");
        String message = (String) body.get("message");
        if (code == 0) {
            logger.debug("결제스케줄 성공!");
        } else {
            logger.debug("결제스케줄 실패!");
        }
    }

    public void cancelSchedule() {
        //TODO










    }

    /**
     * @param accessToken 액세스토큰
     * @param impUid 결제 완료시 마다 만들어지는 ID
     */
    public void checkPaymentResult(String accessToken, String impUid) {
        String url = "https://api.iamport.kr/payments/" + impUid;
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", accessToken);
        HttpEntity entity = new HttpEntity(null, headers);
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        logger.debug("responseEntity >> {}", responseEntity);
        // 결과 json을 결과 DB에 넣고 필요한 정보는 파싱해서 쓴다.
    }

}
