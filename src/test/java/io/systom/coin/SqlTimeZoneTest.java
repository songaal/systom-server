package io.systom.coin;

import io.systom.coin.utils.DateUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * create joonwoo 2018. 8. 9.
 * 
 */
@SpringBootTest(classes = Application.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SqlTimeZoneTest {

    @Autowired
    private SqlSession sqlSession;

    @Test
    public void timeTest () {

        TestInsert testInsert = new TestInsert();
        testInsert.setCreateTime(DateUtils.getDate().getTime());
        System.out.println("추가전 : " + testInsert.getCreateTime());
        int cnt = sqlSession.insert("test.insertTest", testInsert);
        System.out.println("추가 : " + cnt);

    }


    class TestInsert {

        private Date createTime;

        public String getCreateTime() {
            return new SimpleDateFormat("yyyy-MM-dd").format(createTime);
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }


    }

}