//package io.systom.coin;
//
//import io.systom.coin.exception.OperationException;
//import io.systom.coin.model.PerformanceDaily;
//import org.apache.ibatis.session.SqlSession;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.ConcurrentLinkedQueue;
//
///*
// * create joonwoo 2018. 7. 10.
// *
// */
//@Component
//public class Scheduler {
//
//    private static org.slf4j.Logger logger = LoggerFactory.getLogger(Scheduler.class);
//
//    private ConcurrentLinkedQueue<PerformanceDaily> performanceDailyQueue = new ConcurrentLinkedQueue<>();
//
//
//    @Autowired
//    private SqlSession sqlSession;
//
//    private final int selectLimit = 500;
//
////   TODO 사용자별 수익 정보
////   TODO 요약으로 업데이트
////   TODO 월 수익 업데이트
//
//    @Scheduled(fixedDelay= 3 * 60 * 60)
//    public void convertToSummary() {
//
//
////        retrievePerformanceDaily();
//
//
//    }
//
//    protected void retrievePerformanceDaily() {
//        try {
//
//            sqlSession.selectList("performance.retrievePerformanceDaily", selectLimit);
//
//
//        } catch (Exception e) {
//            logger.error("", e);
//            throw new OperationException("[FAIL] SCHEDULER Run Execute Error.");
//        }
//    }
//
//
//}