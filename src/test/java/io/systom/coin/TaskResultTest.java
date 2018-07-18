package io.systom.coin;

import com.amazonaws.services.ecs.model.ClientException;
import com.google.gson.Gson;
import io.systom.coin.model.TaskResult;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * create joonwoo 2018. 7. 18.
 * 
 */
public class TaskResultTest {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(TaskResultTest.class);

    @Test
    public void taskResultTest() {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = null;
        try {
            response = restTemplate.getForObject("https://api.systom.io/result.json", Map.class);
        } catch (ClientException re) {
            logger.error("",re);
        }

        if (response == null) {
            return;
        }

        String resultJson = new Gson().toJson(response);
        TaskResult taskResult = new Gson().fromJson(resultJson, TaskResult.class);
        logger.debug("response: {}", taskResult);
        String start = taskResult.getRequest().getStart();
        String end = taskResult.getRequest().getEnd();

        start = "2015-03-25 12:22:33";
        end = "2018-06-24 12:22:33";

        logger.debug("start: {} end: {}", start, end);

        try {
            List<Map<String, Object>> monthlyLastDateReturnPct = monthlyLastDateReturnPct(start, end, taskResult.getResult().getCumReturns());
            float avgTestReturnPct = 0f;
            for (int i=0; i < monthlyLastDateReturnPct.size(); i++) {
                avgTestReturnPct += (float) monthlyLastDateReturnPct.get(i).get("returnPct");
            }
            logger.debug("result: {}", new Gson().toJson(monthlyLastDateReturnPct));
            logger.debug("testReturnPct: {}", avgTestReturnPct / monthlyLastDateReturnPct.size());
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public List<Map<String, Object>> monthlyLastDateReturnPct(String start, String end, Map<String, Float> cumReturnPct) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar curDate = Calendar.getInstance();
        String endYearMonth = null;
        try {
            curDate.setTime(dateFormat.parse(start.replace("-", "")));
            endYearMonth = end.replace("-", "").substring(0, 6);
        } catch (ParseException e) {
            logger.error("parsing error", e);
            throw e;
        }

        List<Map<String, Object>> lastMonthDateList = new ArrayList<>();
        for (int m = 0; m < 100; m++) {
            int date = curDate.getActualMaximum(Calendar.DAY_OF_MONTH);
            curDate.set(Calendar.DATE, date);
            String lastDate = dateFormat.format(curDate.getTime());
            date = Integer.parseInt(lastDate.substring(6, 8));
            for (int d = date; d >= 1; d--) {
                curDate.set(Calendar.DATE, d);
                lastDate = dateFormat.format(curDate.getTime());
                if (cumReturnPct.get(lastDate) != null) {
                    Map<String, Object> monthlyReturnPct = new HashMap<>();
                    monthlyReturnPct.put("date", lastDate);
                    monthlyReturnPct.put("returnPct", cumReturnPct.get(lastDate));
                    lastMonthDateList.add(monthlyReturnPct);
                    break;
                }
            }
            if (Integer.parseInt(lastDate.substring(0, 6)) >= Integer.parseInt(endYearMonth)) {
                break;
            } else {
                curDate.add(Calendar.MONTH, 1);
            }
        }
        return lastMonthDateList;
    }

}