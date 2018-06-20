package io.systom.coin.utils;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.nio.charset.Charset;

public class RestUtils {

    private static Logger logger = LoggerFactory.getLogger(RestUtils.class);

    private final static String AGENT_HEADER = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)" +
            " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36";

    public static <T> T getObject(String url, Class<T> clazz) {
        Gson gson = new Gson();
        return getObject(url, clazz, gson);
    }

    public static <T> T getObject(String url, Type objectType) {
        Gson gson = new Gson();
        return getObject(url, objectType, gson);
    }

    public static <T> T getObject(String url, Class<T> clazz, Gson gson) {
        try {
            String resultJson = getResultString(url);
            T transaction = gson.fromJson(resultJson, clazz);
            return transaction;
        } catch (Throwable t) {
            logger.error("Cannot fetch data : {}", t.getMessage());
        }
        return null;
    }

    public static <T> T getObject(String url, Type objectType, Gson gson) {
        try {
            String resultJson = getResultString(url);
            T transaction = gson.fromJson(resultJson, objectType);
            return transaction;
        } catch (Throwable t) {
            logger.error("Cannot fetch data : {}", t.getMessage());
        }

        return null;
    }

    public static <T> String getResultString(String url) {
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        headers.set("user-agent", AGENT_HEADER);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (responseEntity.getStatusCode().value() == 401) {
            return null;
        }

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Api error :" + responseEntity.toString());
        }

        return responseEntity.getBody();
    }




}
