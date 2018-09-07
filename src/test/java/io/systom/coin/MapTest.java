package io.systom.coin;

import com.google.gson.Gson;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/*
 * create joonwoo 2018. 9. 6.
 * 
 */
public class MapTest {

    @Test
    public void jsonTest() {
        Random random = new Random();
        Float f = random.nextFloat();
        Float k = random.nextFloat();
        Map<String, Float> monthlyInitCash = new HashMap<>();
        monthlyInitCash.put("USDT", f);
        monthlyInitCash.put("KRW", k);

        String aa = new Gson().toJson(monthlyInitCash);
        System.out.println(aa);

    }
}