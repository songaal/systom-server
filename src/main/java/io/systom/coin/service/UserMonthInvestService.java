package io.systom.coin.service;

import io.systom.coin.exception.OperationException;
import io.systom.coin.model.UserMonthlyInvest;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

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
        List<UserMonthlyInvest> tmpMonthInvest = null;
        try {
            tmpMonthInvest = sqlSession.selectList("userMonthlyInvest.retrieveUserMonthInvestList", userId);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }

        String lastDate = new SimpleDateFormat("yyyyMM").format(Calendar.getInstance().getTime());
        int lastYear = Integer.parseInt(lastDate.substring(0, 4));
        int lastMonth = Integer.parseInt(lastDate.substring(4, 6));
        boolean isChangeYear = false;
        List<UserMonthlyInvest> monthlyInvestList = new ArrayList<>();
        int tmpMonthCursor = tmpMonthInvest.size();
        float sumCash = 0;
        for (int y = lastYear; y >= lastYear - 1; y--) {
            if (isChangeYear) {
                lastMonth = 12;
            }
            for (int m = lastMonth; m >= 1; m--) {
                boolean isAppended = false;
                if (tmpMonthInvest != null) {
                    for (int i = tmpMonthCursor; i > 0; i--){
                        if (i == tmpMonthInvest.size()) {
                            sumCash = tmpMonthInvest.get(i - 1).getSumCash();
                        }
                        lastDate = String.valueOf(y) + (m < 10 ? "0" + m : m);
                        if(tmpMonthInvest.get(i - 1).getDate().equals(lastDate)) {
                            monthlyInvestList.add(tmpMonthInvest.get(i - 1));
                            isAppended = true;
                            tmpMonthCursor--;
                            break;
                        }
                    }
                }
                if (!isAppended) {
                    UserMonthlyInvest tmp = new UserMonthlyInvest();
                    tmp.setDate(String.valueOf(y) + String.valueOf(m < 10 ? "0" + m : m));
                    tmp.setMonthlyReturn(0);
                    tmp.setMonthlyReturnPct(0);
                    tmp.setInitCash(0);
                    tmp.setUserId(userId);
                    tmp.setSumCash(sumCash);
                    monthlyInvestList.add(tmp);
                }
                if (monthlyInvestList.size() == 6) {
                    break;
                }
            }
            if (monthlyInvestList.size() == 6) {
                break;
            } else {
                isChangeYear = true;
            }
        }
        Collections.reverse(monthlyInvestList);
        return monthlyInvestList;
    }

    public Map<String, Object> getDailyInvest(String userId) {
        Map<String, Object> dailyInvest = null;
        try {
            dailyInvest = sqlSession.selectOne("userMonthlyInvest.getDailyInvest", userId);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }
        if (dailyInvest == null) {
            dailyInvest = new HashMap<>();
            dailyInvest.put("cash", 0f);
            dailyInvest.put("equity", 0f);
        }
        return dailyInvest;
    }
}