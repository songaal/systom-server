package io.systom.coin;

import com.google.gson.Gson;
import io.systom.coin.exception.OperationException;
import io.systom.coin.model.TraderTask;
import io.systom.coin.model.TraderTaskResult;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/*
 * create joonwoo 2018. 7. 18.
 * 
 */
public class TraderTraderTaskResultTest {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(TraderTraderTaskResultTest.class);

    private TraderTask getTask() {
        TraderTask traderTask = new TraderTask();
//        traderTask.setSessionType(TraderTask.SESSION_TYPES.backtest.name());
        traderTask.setStrategyId(18);
        traderTask.setExchange("binance");
        traderTask.setCoinUnit("eth");
        traderTask.setBaseUnit("btc");
        traderTask.setCashUnit("usdt");
        traderTask.setStartDate("20180403");
        traderTask.setEndDate("20180530");
        return traderTask;
    }

    private TraderTaskResult testTaskResult(TraderTask traderTask) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/result.json";
        String json = restTemplate.getForObject(url, String.class);
        return new Gson().fromJson(json, TraderTaskResult.class);
    }

    @Test
    public void createGoodsBackTest() throws ParseException {
        TraderTask traderTask = getTask();
        TraderTaskResult traderTaskResult = testTaskResult(traderTask);

        if (!"success".equalsIgnoreCase(traderTaskResult.getStatus())) {
            throw new OperationException("[Fail] BackTest");
        }
//        "2018-01-01 12:33:23"
        String startDate = traderTaskResult.getRequest().getStart();
        String endDate = traderTaskResult.getRequest().getEnd();
        Map<String, Float> cumReturns = traderTaskResult.getResult().getCumReturns();
        monthlyLastDateReturnPct(cumReturns);
//        logger.debug("{}", monthlyLastDateReturnPctList);
//        float avgTestReturnPct = 0f;
//        for (int i=0; i < monthlyLastDateReturnPctList.size(); i++) {
//            avgTestReturnPct += (float) monthlyLastDateReturnPctList.get(i).get("returnPct");
//        }

//        try {
//            Map<String, Object> params = new HashMap<>();
//            params.put("goodsId", traderTask.getGoodsId());
//            params.put("testReturnPct", Float.parseFloat(String.format("%.2f", (avgTestReturnPct / monthlyLastDateReturnPctList.size()))));
//            params.put("testMonthlyReturn", gson.toJson(monthlyLastDateReturnPctList));
////            int changeRow = sqlSession.update("goods.createGoodsBackTest", params);
////            if (changeRow != 1) {
////                logger.error("[FAIL] sql execute. changeRow: {}, params: {}", changeRow, params);
////                throw new OperationException("[FAIL] sql execute. changeRow: {}" + changeRow);
////            }
//
////            InvestGoods botInvestGoods = investGoodsService.findInvestGoodsByUser(traderTask.getGoodsId(), BOT_USER_ID);
////            tradeService.insertTradeHistory(botInvestGoods.getId(), traderTaskResult.getResult().getTradeHistory());
//
//        } catch (Exception e) {
//            logger.error("", e);
//            throw new OperationException("[FAIL] sql execute");
//        }

//        return goodsService.getGoods(traderTask.getGoodsId(), traderTask.getAuthorId());
    }

    private Map<String, Float> monthlyLastDateReturnPct(Map<String, Float> cumReturnPct) throws ParseException {
        String tmpDate = null;

        Map<String, Float> lastRp = new LinkedHashMap<>();
        Iterator<Map.Entry<String, Float>> iterator = cumReturnPct.entrySet().iterator();

        while(iterator.hasNext()) {
            Map.Entry<String, Float> cumReturn = iterator.next();
            String yyyymm = cumReturn.getKey();
            if (!Pattern.matches("^[0-9]*$", yyyymm)) {
                continue;
            } else {
                yyyymm = yyyymm.substring(0, 6);
            }

            float rp = cumReturn.getValue().floatValue();
            lastRp.put(yyyymm, rp);
            if (tmpDate == null || !tmpDate.equals(yyyymm)) {
                tmpDate = yyyymm;
            }
        }
        float diff = 0f;
        iterator = lastRp.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, Float> lastCumReturn = iterator.next();
            float tmpRp = lastCumReturn.getValue();
            lastCumReturn.setValue(tmpRp - diff);
            diff = tmpRp;
        }

        return lastRp;
    }





















//
//
//
//
//
//
//
//
//
//
//    @Test
//    public void taskResultTest() {
//        RestTemplate restTemplate = new RestTemplate();
//        Map<String, Object> response = null;
//        try {
//            response = restTemplate.getForObject("https://api.systom.io/result.json", Map.class);
//        } catch (ClientException re) {
//            logger.error("",re);
//        }
//
//        if (response == null) {
//            return;
//        }
//
//        String resultJson = new Gson().toJson(response);
//        TraderTaskResult taskResult = new Gson().fromJson(resultJson, TraderTaskResult.class);
//        logger.debug("response: {}", taskResult);
//        String start = taskResult.getRequest().getStart();
//        String end = taskResult.getRequest().getEnd();
//
//        start = "2015-03-25 12:22:33";
//        end = "2018-06-24 12:22:33";
//
//        logger.debug("start: {} end: {}", start, end);
//
//        try {
//            List<Map<String, Object>> monthlyLastDateReturnPct = monthlyLastDateReturnPct(start, end, taskResult.getResult().getCumReturns());
//            float avgTestReturnPct = 0f;
//            for (int i=0; i < monthlyLastDateReturnPct.size(); i++) {
//                avgTestReturnPct += (float) monthlyLastDateReturnPct.get(i).get("returnPct");
//            }
//            logger.debug("result: {}", new Gson().toJson(monthlyLastDateReturnPct));
//            logger.debug("testReturnPct: {}", avgTestReturnPct / monthlyLastDateReturnPct.size());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public List<Map<String, Object>> monthlyLastDateReturnPct(String start, String end, Map<String, Float> cumReturnPct) throws ParseException {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
//        Calendar curDate = Calendar.getInstance();
//        String endYearMonth = null;
//        try {
//            curDate.setTime(dateFormat.parse(start.replace("-", "")));
//            endYearMonth = end.replace("-", "").substring(0, 6);
//        } catch (ParseException e) {
//            logger.error("parsing error", e);
//            throw e;
//        }
//
//        List<Map<String, Object>> lastMonthDateList = new ArrayList<>();
//        for (int m = 0; m < 100; m++) {
//            int date = curDate.getActualMaximum(Calendar.DAY_OF_MONTH);
//            curDate.set(Calendar.DATE, date);
//            String lastDate = dateFormat.format(curDate.getTime());
//            date = Integer.parseInt(lastDate.substring(6, 8));
//            for (int d = date; d >= 1; d--) {
//                curDate.set(Calendar.DATE, d);
//                lastDate = dateFormat.format(curDate.getTime());
//                if (cumReturnPct.get(lastDate) != null) {
//                    Map<String, Object> monthlyReturnPct = new HashMap<>();
//                    monthlyReturnPct.put("date", lastDate);
//                    monthlyReturnPct.put("returnPct", cumReturnPct.get(lastDate));
//                    lastMonthDateList.add(monthlyReturnPct);
//                    break;
//                }
//            }
//            if (Integer.parseInt(lastDate.substring(0, 6)) >= Integer.parseInt(endYearMonth)) {
//                break;
//            } else {
//                curDate.add(Calendar.MONTH, 1);
//            }
//        }
//        return lastMonthDateList;
//    }

}