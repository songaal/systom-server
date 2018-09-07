package io.systom.coin.service;

import com.google.gson.Gson;
import io.systom.coin.exception.OperationException;
import io.systom.coin.model.DailyInvestState;
import io.systom.coin.model.MonthlyPerformanceSummary;
import io.systom.coin.model.UserMonthlyInvest;
import io.systom.coin.utils.CurrencyUtils;
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
        for (int y = lastYear; y >= lastYear - 1; y--) {
            if (isChangeYear) {
                lastMonth = 12;
            }
            for (int m = lastMonth; m >= 1; m--) {
                boolean isAppended = false;
                if (tmpMonthInvest != null) {
                    for (int i = tmpMonthCursor; i > 0; i--){
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
                    tmp.setInitCash("{\"KRW\":0,\"USDT\":0,}");
                    tmp.setMonthlyReturn("{\"KRW\":0,\"USDT\":0,}");
                    tmp.setMonthlyReturnPct(0);
                    tmp.setUserId(userId);
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
        List<DailyInvestState> DailyInvestStateList = null;
        Map<String, Object> investState = new HashMap<>();
        try {
            DailyInvestStateList = sqlSession.selectList("userMonthlyInvest.getDailyInvest", userId);
        } catch (Exception e){
            logger.error("", e);
            throw new OperationException("[FAIL] SQL Execute.");
        }

        Iterator<DailyInvestState> investStateIterator = DailyInvestStateList.iterator();
        float usdtInitCash = 0;
        float krwInitCash = 0;
        float usdtEquity = 0;
        float krwEquity = 0;

        while(investStateIterator.hasNext()) {
            DailyInvestState tmp = investStateIterator.next();
            if("USDT".equalsIgnoreCase(tmp.getCashUnit())) {
                usdtInitCash += tmp.getInitCash();
                usdtEquity   += tmp.getEquity();
            } else if ("KRW".equalsIgnoreCase(tmp.getCashUnit())) {
                krwInitCash += tmp.getInitCash();
                krwEquity   += tmp.getEquity();
            }
        }

        Gson gson = new Gson();
        Map<String, Float> initCash = new HashMap<>();
        initCash.put("USDT", usdtInitCash + (krwInitCash / CurrencyUtils.getCurrencyRate("KRW")));
        initCash.put("KRW", krwInitCash + (usdtInitCash * CurrencyUtils.getCurrencyRate("KRW")));
        investState.put("initCash", gson.toJson(initCash));

        Map<String, Float> equity = new HashMap<>();
        equity.put("USDT", usdtEquity + (krwEquity / CurrencyUtils.getCurrencyRate("KRW")));
        equity.put("KRW", krwEquity + (usdtEquity * CurrencyUtils.getCurrencyRate("KRW")));
        investState.put("equity", gson.toJson(equity));

        return investState;
    }

    public void updateMonthlyCalculation(String userId) {
        List<String> user = new ArrayList<>();
        user.add(userId);
        updateMonthlyCalculation(user);
    }
    public void updateMonthlyCalculation(List<String> userList) {
        float currencyRate = CurrencyUtils.getCurrencyRate("KRW");

        int userSize = userList.size();
        for(int i=0; i<userSize; i++) {
            List<MonthlyPerformanceSummary> performanceSummaryList = sqlSession.selectList("userMonthlyInvest.getUserMonthlyInvest", userList.get(i));

            Iterator<MonthlyPerformanceSummary> iterator = performanceSummaryList.iterator();

            float usdtInitCash = 0;
            float usdtEquity = 0;
            int krwInitCash = 0;
            int krwEquity = 0;
            String monthly = null;
//          1.  cash_unit별로 투자금액, 수익금액을 합한다.
            while(iterator.hasNext()) {
                MonthlyPerformanceSummary summary = iterator.next();
                if (monthly == null) {
                    monthly = summary.getDate();
                }
                String currencyKey = summary.getCashUnit();
                if ("USDT".equalsIgnoreCase(currencyKey)) {
                    usdtInitCash += summary.getInitCash();
                    usdtEquity += summary.getEquity();
                } else if ("KRW".equalsIgnoreCase(currencyKey)){
                    krwInitCash += summary.getInitCash();
                    krwEquity += summary.getEquity();
                }
            }

//          2. 환율 적용.
//            - USDT
            float totalUsdtInitCash = usdtInitCash;
            totalUsdtInitCash += krwInitCash / currencyRate;
            float totalUsdtEquity = usdtEquity;
            totalUsdtEquity += krwEquity / currencyRate;
//            - KRW
            float totalKrwInitCash = krwInitCash;
            totalKrwInitCash += usdtInitCash * currencyRate;
            float totalKrwEquity = krwEquity;
            totalKrwEquity += usdtEquity * currencyRate;

            float usdtPct = calculateReturnPct(totalUsdtInitCash, totalUsdtEquity);
            float krwPct = calculateReturnPct(totalKrwInitCash, totalKrwEquity);

            Map<String, Float> monthlyInitCash = new HashMap<>();
            monthlyInitCash.put("USDT", totalUsdtInitCash);
            monthlyInitCash.put("KRW", totalKrwInitCash);

            Map<String, Float> monthlyEquity = new HashMap<>();
            monthlyEquity.put("USDT", totalUsdtEquity);
            monthlyEquity.put("KRW", totalKrwEquity);

            Map<String, Float> monthlyReturn = new HashMap<>();
            monthlyReturn.put("USDT", totalUsdtEquity - totalUsdtInitCash);
            monthlyReturn.put("KRW", totalKrwEquity - totalKrwInitCash);

            Map<String, Float> monthlyReturnPct = new HashMap<>();
            monthlyReturnPct.put("USDT", usdtPct);
            monthlyReturnPct.put("KRW", krwPct);

//          3. DB 업데이트
            try {
                Gson gson = new Gson();
                UserMonthlyInvest userMonthlyInvest = new UserMonthlyInvest();
                userMonthlyInvest.setUserId(userList.get(i));
                userMonthlyInvest.setDate(monthly);
                userMonthlyInvest.setInitCash(gson.toJson(monthlyInitCash));
                userMonthlyInvest.setEquity(gson.toJson(monthlyEquity));
                userMonthlyInvest.setMonthlyReturn(gson.toJson(monthlyReturn));
                userMonthlyInvest.setMonthlyReturnPct(usdtPct);

                logger.debug("사용자 [{}] 월투자금: {}, 총자산: {}, 월수익률: {}, 월수익금: {}", userList.get(i),
                        userMonthlyInvest.getInitCash(),
                        userMonthlyInvest.getEquity(),
                        userMonthlyInvest.getMonthlyReturnPct(),
                        userMonthlyInvest.getMonthlyReturn());
                sqlSession.insert("userMonthlyInvest.updateMonthlyInvest", userMonthlyInvest);
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

    protected float calculateReturnPct(float initCash, float equity) {
        float pct = 0;
        try {
            pct = (equity - initCash) / initCash * 100;
            if (Double.isNaN(pct)) {
                pct = 0;
            }
        } catch (Exception e){
            // ignore
        }
        return pct;
    }

}