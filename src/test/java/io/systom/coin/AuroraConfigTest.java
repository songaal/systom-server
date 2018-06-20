package io.systom.coin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

/*
 * create joonwoo 2018. 3. 22.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class AuroraConfigTest {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(AuroraConfigTest.class);
    @Autowired
    private DataSource dataSource;
    @Test
    public void connectionTest(){


        logger.info("dataSource : {}", dataSource);

    }

}