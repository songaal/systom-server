package io.systom.coin;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.*;

/*
 * create joonwoo 2018. 8. 9.
 * 
 */
@SpringBootTest(classes = Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SqlTimeZoneTest {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(SqlTimeZoneTest.class);
    @Autowired
    private SqlSession sqlSession;


    @Test
    public void selectTest() {
        TestInsert testInsert = new TestInsert();
        testInsert.setTimeStamp(new Date());
        int cnt = sqlSession.insert("test.insertTest", testInsert);
        System.out.println("추가 : " + cnt);

        List<Map<String, Object>> list = sqlSession.selectList("test.selectTest");
        logger.info("{}", list);
    }




    @Test
    public void timezoneListTest () {
        String[] timezoneList = TimeZone.getAvailableIDs();
        int size = timezoneList.length;
        for (int i=0; i < size; i++) {
            TimeZone t = TimeZone.getTimeZone(timezoneList[i]);
            Locale.setDefault(Locale.ENGLISH);

            logger.info("timezone Name: {}", t.getDisplayName());


        }

    }

    @Test
    public void timeZoneTest() {


//        String timeZone = "Asia/Seoul";
        String timeZone = "UTC";
//        String timeZone = "America/New_York";

        TimeZone tz = TimeZone.getTimeZone(timeZone);

        int offsetTime = tz.getOffset(new Date().getTime());
        logger.info("offsetTime: {}", offsetTime);

        int hour = offsetTime / 1000 / 60 / 60;
        logger.info("offsetTime hour: {}", hour);

        int min = hour == 0 ? 0 : offsetTime / 1000 / 60 % hour;
        logger.info("offsetTime minute: {}", min);


//        Calendar calendar = Calendar.getInstance(tz);
//        logger.info("seoul: {}", calendar.getTime());





    }




    @Test
    public void timeTest () {

        TestInsert testInsert = new TestInsert();
        testInsert.setTimeStamp(new Date());
        int cnt = sqlSession.insert("test.insertTest", testInsert);
        System.out.println("추가 : " + cnt);

    }

    @Test
    public void nowTest() {
        Map<String, Object> m = sqlSession.selectOne("test.selectNow");
        logger.info("{}", m);
    }


    class TestInsert {

        private Date createTime;
        private Date timeStamp;

        public Date getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(Date timeStamp) {
            this.timeStamp = timeStamp;
        }

        public String getCreateTime() {
            return new SimpleDateFormat("yyyy-MM-dd").format(createTime);
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }


    }

}