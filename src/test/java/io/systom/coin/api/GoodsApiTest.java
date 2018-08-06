package io.systom.coin.api;

import io.systom.coin.Application;
import io.systom.coin.model.Goods;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/*
 * create joonwoo 2018. 8. 6.
 * 
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class GoodsApiTest {

    String goodsAPIUrl = "http://localhost:8080/v1/goods";
    RestTemplate rest = new RestTemplate();

    String collectStart = null;
    String collectEnd = null;
    String investStart = null;
    String investEnd = null;
    String testStart = null;
    String testEnd = null;

    @Test
    public void init () {
        Calendar collectStartDate = Calendar.getInstance();

        Calendar collectEndDate = Calendar.getInstance();
        collectEndDate.add(Calendar.DATE, 1);

        Calendar investStartDate = Calendar.getInstance();
        investStartDate.add(Calendar.DATE, 1);

        Calendar investEndDate = Calendar.getInstance();
        investEndDate.add(Calendar.DATE, 2);

        Calendar testStartDate = Calendar.getInstance();
        testStartDate.add(Calendar.DATE, -7);

        Calendar testEndDate = Calendar.getInstance();
        testEndDate.add(Calendar.DATE, -1);

        collectStart = new SimpleDateFormat("YYYYMMdd").format(collectStartDate.getTime());
        collectEnd = new SimpleDateFormat("YYYYMMdd").format(collectEndDate.getTime());
        investStart = new SimpleDateFormat("YYYYMMdd").format(investStartDate.getTime());
        investEnd = new SimpleDateFormat("YYYYMMdd").format(investEndDate.getTime());
        testStart = new SimpleDateFormat("YYYYMMdd").format(testStartDate.getTime());
        testEnd = new SimpleDateFormat("YYYYMMdd").format(testEndDate.getTime());
    }


    @Test
    public void createGoodsTest() {
        Goods goods = new Goods();
        goods.setStrategyId(20);
        goods.setVersion(1);
        goods.setDescription("API 테스트 상품 설명");
        goods.setName("API 테스트 상품");
        goods.setExchange("binance");
        goods.setCoinUnit("eth");
        goods.setBaseUnit("btc");
        goods.setCash(100000f);
        goods.setCashUnit("usdt");
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity entity = new HttpEntity(goods, httpHeaders);
        ResponseEntity<String> responseEntity = rest.postForEntity(goodsAPIUrl, entity, String.class);
        Assert.assertEquals(200, responseEntity.getStatusCodeValue());





    }

}