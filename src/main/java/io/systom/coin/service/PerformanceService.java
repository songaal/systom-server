package io.systom.coin.service;

import io.systom.coin.exception.OperationException;
import io.systom.coin.model.PerformanceDaily;
import io.systom.coin.model.PerformanceSummary;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * create joonwoo 2018. 7. 10.
 * 
 */
@Service
public class PerformanceService {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(PerformanceService.class);

    @Autowired
    private SqlSession sqlSession;

    public void insertPerformanceSummary(PerformanceSummary performance) {
        try {
            int changeRow = sqlSession.insert("performance.insertPerformanceSummary", performance);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

    public void deletePerformanceSummary(Integer investId) {
        try {
            int changeRow = sqlSession.delete("performance.deletePerformanceSummary", investId);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

    public void deletePerformanceDaily(Integer investId) {
        try {
            int changeRow = sqlSession.delete("performance.deletePerformanceDaily", investId);
            if (changeRow != 1) {
                throw new OperationException("[FAIL] SQL Execute. change row: " + changeRow);
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

    public PerformanceSummary getPerformanceSummary(Integer investId) {
        try {
            return sqlSession.selectOne("performance.getPerformanceSummary", investId);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

    public List<PerformanceDaily> getPerformanceDailyList(Integer investId) {
        try {
            return sqlSession.selectList("performance.getPerformanceDailyList", investId);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
    }

}