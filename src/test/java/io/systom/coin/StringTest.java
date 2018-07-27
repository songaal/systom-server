package io.systom.coin;

import org.junit.Test;
import org.slf4j.LoggerFactory;

/*
 * create joonwoo 2018. 7. 27.
 * 
 */
public class StringTest {
    org.slf4j.Logger logger = LoggerFactory.getLogger(StringTest.class);
    @Test
    public void strTest() {
        logger.info("input => true: {}", "true".equalsIgnoreCase("true"));
        logger.info("input => false: {}", "true".equalsIgnoreCase("false"));
        logger.info("input => null: {}", "true".equalsIgnoreCase(null));


    }
}