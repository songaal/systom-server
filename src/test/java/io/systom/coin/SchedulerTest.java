package io.systom.coin;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/*
 * create joonwoo 2018. 6. 14.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class SchedulerTest {

    @Autowired
    private SqlSession sqlSession;
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(SchedulerTest.class);

    @Test
    public void scTest(){
//        List<StrategyStatus> statusList = sqlSession.selectList("strategyStatus.retrieveUsedStatus");
//        int statusSize = statusList.size();
//        long nowTime = new Date().getTime();
//        for (int i=0; i < statusSize; i++){
//            StrategyStatus status = statusList.get(i);
//            Calendar orderTime = Calendar.getInstance();
//            orderTime.setTime(status.getTime());
//            orderTime.add(Calendar.MONTH, 1);
//            long expireTime = orderTime.getTime().getTime();
//            if (expireTime <= nowTime){
//                logger.info("ExpireStatus >> Status: {}", status);
//                sqlSession.update("strategyStatus.updateUnusedStatus", status);
//            }
//        }
    }
}