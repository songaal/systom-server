//package io.systom.coin.service.backup;//package io.gncloud.coin.server.service;
//
//import io.systom.coin.exception.AuthenticationException;
//import io.systom.coin.exception.OperationException;
//import io.systom.coin.exception.ParameterException;
//import io.systom.coin.exception.RequestException;
//import io.systom.coin.model.Goods;
//import io.systom.coin.model.StrategyDeployVersion;
//import io.systom.coin.model.Task;
//import io.systom.coin.service.StrategyDeployService;
//import io.systom.coin.service.TaskService;
//import io.systom.coin.utils.StringUtils;
//import org.apache.ibatis.session.SqlSession;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//import java.util.concurrent.TimeoutException;
//
///*
// * create joonwoo 2018. 6. 8.
// *
// */
//@Service
//public class MarketplaceService2 {
//    private static org.slf4j.Logger logger = LoggerFactory.getLogger(MarketplaceService2.class);
//
//    @Value("${backtest.influxTimeDifference}")
//    private int influxTimeDifference;
//
//    @Autowired private SqlSession sqlSession;
//    @Autowired private StrategyDeployService strategyDeployService;
//    @Autowired private TaskService taskService;
//
//    @Transactional
//    public Goods releases(String userId, Goods target) throws ParameterException, OperationException, AuthenticationException, RequestException, TimeoutException {
//        Integer strategyId = target.getStrategyId();
//        Integer version = target.getVersion();
//        String symbol = target.getSymbol();
//        String exchange = target.getExchange();
//        String timeInterval = target.getTimeInterval();
//        String startTime = target.getStartTime();
//
//        if ( strategyId == null || version == null
//                || StringUtils.isEmpty(exchange) || StringUtils.isBlank(exchange)
//                || StringUtils.isEmpty(timeInterval) || StringUtils.isBlank(timeInterval)
//                || StringUtils.isEmpty(symbol) || StringUtils.isBlank(symbol)
//                || StringUtils.isEmpty(startTime)|| StringUtils.isBlank(startTime)){
//            throw new ParameterException("required");
//        }
//
//        StrategyDeployVersion targetStrategy = new StrategyDeployVersion();
//        targetStrategy.setUserId(userId);
//        targetStrategy.setId(strategyId);
//        targetStrategy.setVersion(version);
//        StrategyDeployVersion registerDeployVersion = strategyDeployService.getDeployVersion(targetStrategy);
//        if (registerDeployVersion == null) {
//            throw new ParameterException("id, version");
//        } else if (!registerDeployVersion.getUserId().equals(userId)) {
//            throw new AuthenticationException();
//        }
//
//        List<Goods> registerGoodsList = findGoodsByStrategy(targetStrategy);
//        int goodsSize = registerGoodsList.size();
//        for (int i=0; i < goodsSize; i++) {
//            String regSymbol = registerGoodsList.get(i).getSymbol();
//            if (regSymbol.equals(symbol)) {
//                throw new RequestException("This symbol is already registered.");
//            }
//        }
//        try {
//            int changeRow = sqlSession.insert("marketplace.insertGoods", target);
//            if (changeRow != 1) {
//                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
//            }
//        } catch (Exception e) {
//            logger.error("", e);
//            throw new OperationException("[FAIL] SQL Execute.");
//        }
//
//        Task backTestTask = new Task();
//        backTestTask.setStrategyId(strategyId);
//        backTestTask.setExchange(exchange);
//        backTestTask.setSymbol(symbol);
//        backTestTask.setStartTime(startTime);
//
////        TaskResult backTestResult = taskService.syncBackTest(backTestTask);
//
////        TODO 에이전트 실행
//
////        TODO 최종 정보 저장
//
//        return null;
//    }
//
//    protected String backTestEndTime() {
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.MINUTE, influxTimeDifference * -1);
//        return new SimpleDateFormat("%Y").format(calendar.getTime());
//    }
//
//    public Goods getGoods(int id) {
//        Goods registerGoods = null;
//        try {
//            registerGoods = sqlSession.selectOne("marketplace.getReleasesGoods", id);
//        } catch (Exception e) {
//            logger.error("", e);
//        }
//        return registerGoods;
//    }
//
//    public List<Goods> findGoodsByStrategy(StrategyDeployVersion deployVersion) throws ParameterException, OperationException {
//        List<Goods> registerGoodsList = null;
//        try {
//            registerGoodsList = sqlSession.selectOne("marketplace.getReleasesGoods", deployVersion);
//        } catch (Exception e) {
//            logger.error("", e);
//            throw new OperationException("[FAIL] SQL Execute.");
//        }
//        return registerGoodsList == null ? new ArrayList<>() : registerGoodsList;
//    }
//
//
//
//}