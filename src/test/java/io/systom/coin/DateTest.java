package io.systom.coin;

import org.junit.Test;

import java.text.SimpleDateFormat;

/*
 * create joonwoo 2018. 3. 21.
 * 
 */
public class DateTest {

    @Test
    public void timeDate(){
        new SimpleDateFormat("yyyy-mm-dd").format(null);
    }


    @Test
    public void equalsTest() {
        System.out.println("aa".equals(null));
    }
}