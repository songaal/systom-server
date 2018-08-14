package io.systom.coin;

import io.systom.coin.scheduler.LiveTaskObserveScheduler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/*
 * create joonwoo 2018. 8. 14.
 * 
 */
@SpringBootTest(classes = Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class TaskObserveTest {

    @Autowired
    private LiveTaskObserveScheduler liveTaskObserveScheduler;

    @Test
    public void taskTest (){
        liveTaskObserveScheduler.task();
    }
}