package io.systom.coin.scheduler;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/*
 * create joonwoo 2018. 8. 14.
 * 
 */
@Component
public class LiveTaskObserveScheduler {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(LiveTaskObserveScheduler.class);
    private String isLiveTaskObserve;

    @Autowired
    private SqlSession sqlSession;

    private Map<String, Object> taskStatus = new HashMap<>();

    public void task () {
        if (!"true".equalsIgnoreCase(isLiveTaskObserve)) {
            logger.debug("LiveTaskObserve Disabled");
            return;
        }
        //    TODO 라이브 Task 감시 스케쥴러 DB 조회 -> 종료 작업 알림 및 재시작

    }

}