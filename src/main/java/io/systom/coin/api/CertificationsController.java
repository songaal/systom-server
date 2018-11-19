package io.systom.coin.api;

import com.amazonaws.services.ecs.model.ClientException;
import com.google.gson.Gson;
import io.systom.coin.exception.AbstractException;
import io.systom.coin.exception.OperationException;
import io.systom.coin.exception.ParameterException;
import io.systom.coin.model.Certification;
import io.systom.coin.service.CertificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/certifications")
public class CertificationsController extends AbstractController {
    private static Logger logger = LoggerFactory.getLogger(CertificationsController.class);
    
    @Autowired
    private CertificationService certificationService;

    @Value("${import.impKey}")
    private String impKey;
    @Value("${import.impSecret}")
    private String impSecret;

    private static String impToken;

    @GetMapping
    public ResponseEntity<?> getCertification(@RequestAttribute String userId) {
        try {
            Certification certification = certificationService.getCertification(userId);
            return success(certification);
        } catch(AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable e) {
            logger.error("", e);
            return new OperationException().response();
        }
    }


    @PostMapping
    public ResponseEntity<?> insertCertification(@RequestAttribute String userId, @RequestBody Certification certification) {
        try {
            if (impToken == null) {
                impToken = getImpToken();
            }

            Map<String, Object> data = getCertifications(certification.getImpUid());

            logger.debug("{}", data);
            if (data == null) {
                impToken = getImpToken();
                data = getCertifications(certification.getImpUid());
            }

            Map<String, Object> userInfo = (Map<String, Object>) data.get("response");
            logger.debug("{}", userInfo);

// birth=589384800,
// certified=true,
// certified_at=1542618440,
// foreigner=false,
// gender=male,
// imp_uid=imp_897414984516,
// merchant_uid=ORDER-1542618414845,
// name=김준우,
// origin=http://localhost:3000/account#,
// pg_provider=danal,
// pg_tid=201811191806547541432010,
// unique_in_site=MC0GCCqGSIb3DQIJAyEATQTkXvgvTCcTC+bLkKLRNZ1+D09B32CUKJscLerRr60=,
// unique_key=ak6hGIZDmoSltNEviconCkX5ViO2QxESpl7MFe/AeC9MhwmobWgf0qMOzbBoUUxM4cJxEHQ65Ow+qvpPbQzzJg==

            certification.setUserId(userId);
            certification.setBirth(String.valueOf(userInfo.get("birth")));
            certification.setGender(String.valueOf(userInfo.get("gender")));
            certification.setUniqueInSite(String.valueOf(userInfo.get("unique_in_site")));
            certification.setUniqueKey(String.valueOf(userInfo.get("unique_key")));
            certification.setName(String.valueOf(userInfo.get("name")));
            certification.setMerchantUid(String.valueOf(userInfo.get("merchant_uid")));

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.YEAR, -20);
            if ((calendar.getTime().getTime() / 1000) < Integer.parseInt("589384800")) {
                throw new ParameterException("age");
            }

            certificationService.insertCertification(certification);

            return success(certification);
        } catch(AbstractException e) {
            logger.error("", e);
            return e.response();
        } catch (Throwable e) {
            logger.error("", e);
            return new OperationException().response();
        }
    }



    private String getImpToken () {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        Map<String, Object> params = new HashMap<>();
        params.put("imp_key", impKey);
        params.put("imp_secret", impSecret);
        HttpEntity<?> request = new HttpEntity<>(params, httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> responseEntity = restTemplate.postForEntity("https://api.iamport.kr/users/getToken", request, Map.class);
        return String.valueOf( ((Map<String, Object>) responseEntity.getBody().get("response")).get("access_token"));
    }

    private Map<String, Object> getCertifications(String impUid) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Authorization", impToken);
        Map<String, Object> params = new HashMap<>();
        HttpEntity<?> request = new HttpEntity<>(params, httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> responseEntity = restTemplate.exchange("https://api.iamport.kr/certifications/" + impUid, HttpMethod.GET, request, Map.class);
        return responseEntity.getBody();
    }

}
