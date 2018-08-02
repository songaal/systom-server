package io.systom.coin;

import org.junit.Test;

import java.util.Random;

/*
 * create joonwoo 2018. 8. 2.
 * 
 */
public class NumberRandomTest {

    @Test
    public void randomTest() {
        Random random = new Random();
        System.out.println(Math.abs(random.ints(1).toArray()[0]));
    }

}