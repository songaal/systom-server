package io.systom.coin;

import io.systom.coin.exception.ParameterException;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class YearTest {

    @Test
    public void aa (){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -20);
        if ((calendar.getTime().getTime() / 1000) < Integer.parseInt("589384800")) {
            throw new ParameterException("age");
        }
    }
}
