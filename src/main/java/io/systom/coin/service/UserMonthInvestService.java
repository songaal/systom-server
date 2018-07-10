package io.systom.coin.service;

import io.systom.coin.exception.OperationException;
import io.systom.coin.model.backup.UserMonthlyInvest;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/*
 * create joonwoo 2018. 7. 10.
 * 
 */
@Service
public class UserMonthInvestService {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(UserMonthInvestService.class);

    @Autowired
    private SqlSession sqlSession;

    public List<UserMonthlyInvest> retrieveUserMonthInvestList(String userId) {
        List<UserMonthlyInvest> userMonthlyInvestList = null;
        try {
            userMonthlyInvestList = sqlSession.selectList("userMonthlyInvestList.retrieveUserMonthInvestList", userId);
            if (userMonthlyInvestList == null) {
                userMonthlyInvestList = new ArrayList<>();
            }
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
        return userMonthlyInvestList;
    }


}