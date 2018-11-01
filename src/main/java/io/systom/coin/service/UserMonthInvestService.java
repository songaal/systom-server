package io.systom.coin.service;

import com.google.gson.Gson;
import io.systom.coin.exception.OperationException;
import io.systom.coin.model.DailyInvestState;
import io.systom.coin.model.UserMonthlyInvest;
import io.systom.coin.model.UserMonthlySum;
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
        List<UserMonthlyInvest> tmpMonthInvest;
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
                    tmp.setInitCash("{\"KRW\":0,\"USDT\":0}");
                    tmp.setMonthlyReturn("{\"KRW\":0,\"USDT\":0}");
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
        List<DailyInvestState> DailyInvestStateList;
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
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String month = new SimpleDateFormat("YYYYMM").format(calendar.getTime());

        Map<String, Object> nowMonthParam = new HashMap<>();
        nowMonthParam.put("date", month);
        calendar.add(Calendar.MONTH, -1);
        String prevMonth = new SimpleDateFormat("YYYYMM").format(calendar.getTime());
        Map<String, Object> prevMonthParam = new HashMap<>();
        prevMonthParam.put("date", prevMonth);

        for(int i=0; i<userList.size(); i++) {
            nowMonthParam.put("userId", userList.get(i));
            prevMonthParam.put("userId", userList.get(i));
//            현재 월
            UserMonthlySum nowMonthlySum = sqlSession.selectOne("userMonthlyInvest.getSumUserNowMonthly", nowMonthParam);

            Float usdtInitCash = nowMonthlySum.getUsdtInitCash();
            Float krwInitCash = nowMonthlySum.getKrwInitCash();
            Float usdtRetrun = nowMonthlySum.getUsdtReturn();
            Float krwRetrun = nowMonthlySum.getKrwReturn();

//            환율 적용.
            Float totalUsdtInitCash = usdtInitCash + (krwInitCash / currencyRate);
            Float totalKrwInitCash = krwInitCash + (usdtInitCash * currencyRate);
            Float totalUsdtRetrun = usdtRetrun + (krwRetrun / currencyRate);
            Float totalKrwRetrun = krwRetrun + (usdtRetrun * currencyRate);

//            전월 차감
            UserMonthlySum prevMonthSum = sqlSession.selectOne("userMonthlyInvest.getUserMonthlyInvest", prevMonthParam);
            totalUsdtRetrun = totalUsdtRetrun - prevMonthSum.getUsdtReturn();
            totalKrwRetrun = totalKrwRetrun - prevMonthSum.getKrwReturn();

//            퍼센트 계산.
            float usdtPct = calculateReturnPct(totalUsdtInitCash, totalUsdtRetrun);
            float krwPct = calculateReturnPct(totalKrwInitCash, totalKrwRetrun);

//            DB 업데이트.
            Map<String, Float> monthlyInitCash = new HashMap<>();
            monthlyInitCash.put("USDT", totalUsdtInitCash <= 0 ? 0 : totalUsdtInitCash);
            monthlyInitCash.put("KRW", totalKrwInitCash <= 0 ? 0 : totalKrwInitCash);

            Map<String, Float> monthlyReturn = new HashMap<>();
            monthlyReturn.put("USDT", totalUsdtRetrun);
            monthlyReturn.put("KRW", totalKrwRetrun);

            Map<String, Float> monthlyReturnPct = new HashMap<>();
            monthlyReturnPct.put("USDT", usdtPct);
            monthlyReturnPct.put("KRW", krwPct);

//          3. DB 업데이트
            try {
                Gson gson = new Gson();
                UserMonthlyInvest userMonthlyInvest = new UserMonthlyInvest();
                userMonthlyInvest.setUserId(userList.get(i));
                userMonthlyInvest.setDate(month);
                userMonthlyInvest.setInitCash(gson.toJson(monthlyInitCash));
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

    protected float calculateReturnPct(float initCash, float returns) {
        logger.debug("initCash: {}, returns: {}", initCash, returns);
        float pct = 0;
        try {
//            pct = (returns / initCash) * 100;
            pct = (returns * 100) / initCash;
            if (Double.isNaN(pct)) {
                pct = 0;
            }
        } catch (Exception e){
            // ignore
            logger.warn("[월 계산 수식 에러] initCash:{}, returns: {}", initCash, returns);
        }
        return pct;
    }

    public Map<String, Float> getUserTotalInitCash(String userId) {
        Map<String, Object> initCash = sqlSession.selectOne("userMonthlyInvest.getSumInitCash", userId);
        float sumUsdtInitCash = Float.parseFloat(initCash.get("sum_usdt_init_cash").toString());
        float sumKrwInitCash = Float.parseFloat(initCash.get("sum_krw_init_cash").toString());
        float totalSumUsdtInitCash = sumUsdtInitCash + (sumKrwInitCash / CurrencyUtils.getCurrencyRate("KRW"));
        float totalSumKrwInitCash = sumKrwInitCash + (sumUsdtInitCash * CurrencyUtils.getCurrencyRate("KRW"));
        Map<String, Float> result = new HashMap<>();
        result .put("sumUsdtInitCash", totalSumUsdtInitCash);
        result .put("sumKrwInitCash", totalSumKrwInitCash);
        return result;
    }

}