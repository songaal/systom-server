package io.systom.coin.service;

import com.google.gson.Gson;
import io.systom.coin.model.Card;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    private String accessToken;
    private int expiredAt;

    @Value("${billing.impKey}")
    private String impKey;
    @Value("${billing.impSecret}")
    private String impSecret;

    public String getToken() {
        int now = (int) (System.currentTimeMillis() / 1000);
        if(expiredAt > now + 60) { //60초를 더해서 1분정도 남았을때에도 키를 다시받아오도록 한다.
            //아직 파기전이라면.
            return accessToken;
        }

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
            accessToken = (String) response.get("access_token");
            expiredAt = (int) response.get("expired_at");
            logger.debug("Status[{}] accessToken[{}] expiredAt[{}]", responseEntity.getStatusCode(), accessToken, expiredAt);
            return accessToken;
        } else {
            logger.error("인증에러!");
        }
        return null;
    }

    /**
     * 사용자 신용카드 정보 및 키를 저장한다.
     * @param card 신용카드정보
     */
    public Map registerCard(Card card) {
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
        headers.add("Authorization", getToken());
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
        return body;
    }

    /*
     * 아이엠포트의 카드등록정보를 지우며, 아래 케이스에 사용한다.
     * - 카드삭제시
     * - 회원탈퇴시
     */
    public Map unregisterCard(String customerUid) {

        String url = "https://api.iamport.kr/subscribe/customers/" + customerUid;
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", getToken());
        HttpEntity entity = new HttpEntity(null, headers);
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, entity, Map.class);
        logger.debug("responseEntity >> {}", responseEntity);
        Map body = responseEntity.getBody();
        int code = (int) body.get("code");
        String message = (String) body.get("message");
        if (code == 0) {
            logger.debug("카드삭제 성공!");
        } else {
            logger.debug("카드삭제 실패!");
        }
        return body;
    }

    /**
     * @param customerUid 카드등록시 설정한 UID
     * @param merchantUid 날짜와 순번으로 조합해서 만든다. 결제시 마다 매번 달라야함.
     * @param amount      금액.KRW
     * @param description 상품명
     */
    public Map makePayment(String customerUid, String merchantUid, String amount, String description) {
        String url = "https://api.iamport.kr/subscribe/payments/again";
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", getToken());
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

        return body;
    }


    /**
     * 결제취소
     * @param impUid 결제아이디
     * @return
     */
    public Map cancelPayment(String impUid) {
        String url = "https://api.iamport.kr/payments/cancel";
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", getToken());
        MultiValueMap<String, String> params= new LinkedMultiValueMap<>();
        params.add("imp_uid", impUid);
        HttpEntity entity = new HttpEntity(params, headers);
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        logger.debug("responseEntity >> {}", responseEntity);
        Map body = responseEntity.getBody();
        int code = (int) body.get("code");
        String message = (String) body.get("message");
        Map response = (Map) body.get("response");
        String status = (String) response.get("status");
        if (code == 0 && status.equals("paid")) {
            logger.debug("취소성공!");
        } else {
            logger.debug("취소실패!");
        }

        return body;
    }

    /**
     * 결제시 impUid 가 생성되며, 이 UID 로 다시 조회할수 있다.
     * 예약결제의 경우 iamport 가 호출하는 callback에 impUid가 전달된다.
     * @param impUid 결제 완료시 마다 만들어지는 ID
     */
    public Map getPayment(String impUid) {
        String url = "https://api.iamport.kr/payments/" + impUid;
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", getToken());
        HttpEntity entity = new HttpEntity(null, headers);
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        logger.debug("responseEntity >> {}", responseEntity);
        Map body = responseEntity.getBody();
        // 결과 json을 결과 DB에 넣고 필요한 정보는 파싱해서 쓴다.
        return body;
    }

    public Map schedulePayment(String customerUid, String merchantUid, String amount, String description, Calendar scheduleCal) {

        logger.debug("scheduleCal >> {}", scheduleCal.getTime());
        String scheduleAt = String.valueOf(scheduleCal.getTimeInMillis() / 1000);

        String url = "https://api.iamport.kr/subscribe/payments/schedule";
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", getToken());
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

        return body;
    }

    /**
     * 카드와 관련된 모든 스케줄을 취소한다.
     * @param customerUid 취소할 사용자카드 UID
     */
    public Map cancelScheduledPayment(String customerUid) {
        String url = "https://api.iamport.kr/subscribe/payments/unschedule";
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", getToken());
        HashMap<String, Object> params= new HashMap<>();
        params.put("customer_uid", customerUid);
        HttpEntity entity = new HttpEntity(params, headers);
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

        return body;
    }

}
