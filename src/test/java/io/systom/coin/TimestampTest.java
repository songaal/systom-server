package io.systom.coin;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

public class TimestampTest {

    protected static Logger logger = LoggerFactory.getLogger(TimestampTest.class);

    @Test
    public void testMake() {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        long lastTimestampEnd = cal.getTimeInMillis();
        cal.add(Calendar.MINUTE, -1);
        long lastTimestampStart = cal.getTimeInMillis();

        logger.debug("{}~{}", lastTimestampStart, lastTimestampEnd);
        logger.debug("{}~{}", new Date(lastTimestampStart), new Date(lastTimestampEnd));
    }

    @Test
    public void timeStampTest() {
        Calendar to = Calendar.getInstance();
        to.add(Calendar.MONTH, 4);
//        to.add(Calendar.DATE, 1);
        System.out.println(System.currentTimeMillis());
        System.out.println(to.getTimeInMillis());

    }
}
