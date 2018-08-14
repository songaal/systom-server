package io.systom.coin.scheduler;

import io.systom.coin.config.TradeConfig;
import io.systom.coin.model.Goods;
import io.systom.coin.model.TraderTask;
import io.systom.coin.service.GoodsService;
import io.systom.coin.service.TaskService;
import io.systom.coin.service.TelegramHandler;
import io.systom.coin.utils.EcsUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

import static io.systom.coin.service.GoodsService.DATE_FORMAT;
import static io.systom.coin.service.GoodsService.TIME_FORMAT;

/*
 * create joonwoo 2018. 8. 14.
 * 
 */
@Component
public class LiveTaskObserveScheduler {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(LiveTaskObserveScheduler.class);

    @Autowired
    private SqlSession sqlSession;
    @Autowired
    private EcsUtils ecsUtils;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private TelegramHandler telegramHandler;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TradeConfig tradeConfig;

    @Value("${scheduler.isLiveTaskObserve}")
    private String isLiveTaskObserve;

    private String startMessage = "%s 상품을 작업 시작합니다.";
    private String retryMessage = "%s 상품을 작업 재시작합니다. 재시도: %d/%d";
    private String stopMessage = "%s 상품이 작업 종료상태입니다. 재시도 횟수: %d";
    private String origin = "SYSTOM";

    // GoodsId, Retry
    private Map<Integer, Integer> taskRetry = new HashMap<>();
    private Integer maxRetry = 3;

    @Scheduled(cron = "0 45 12 * * *")
    public void task () {
        if (!"true".equalsIgnoreCase(isLiveTaskObserve)) {
            logger.debug("LiveTaskObserve Disabled");
            return;
        }
        String nowTime = new SimpleDateFormat(DATE_FORMAT + TIME_FORMAT).format(new Date());
        Goods searchGoods = new Goods();
        searchGoods.setInvestStart(nowTime);
        searchGoods.setInvestEnd(nowTime);
        searchGoods.setDisplay(true);
        List<Goods> goodsList = new ArrayList<>();
        int exchangeSize = tradeConfig.getLiveExchange().size();
        for (int i=0; i < exchangeSize; i++) {
            searchGoods.setExchange(tradeConfig.getLiveExchange().get(i));
            List<Goods> tmpGoodsList = goodsService.retrieveGoodsList(searchGoods);
            if (tmpGoodsList != null) {
                goodsList.addAll(tmpGoodsList);
            }
        }
        if (goodsList == null || goodsList.size() == 0) {
            return;
        }
        int goodsSize = goodsList.size();
        List<String> runTaskList = ecsUtils.getRunningTaskList();

        TraderTask traderTask = new TraderTask();
        traderTask.setAction(TraderTask.ACTIONS.start.name());
        traderTask.setSessionType("live");

        for (int i=0; i < goodsSize; i++) {
            Goods tmpGoods = goodsList.get(i);
            traderTask.setGoodsId(tmpGoods.getId());
            String goodsTaskArn = tmpGoods.getTaskEcsId();
            if (goodsTaskArn == null || "".equalsIgnoreCase(goodsTaskArn)) {
                // 투자 진행 기간 중 ECS ARN 없을 시
                String message = String.format(startMessage, tmpGoods.getName());
                telegramHandler.adminSend(origin, message);
                taskService.liveTaskRun(traderTask, true);
                continue;
            }
            boolean isRunning = false;
            int taskSize = runTaskList.size();
            for (int j=0; j < taskSize; j++) {
                String arn = runTaskList.get(j);
                if (goodsTaskArn.equals(arn)) {
                    isRunning = true;
                    break;
                }
            }
            if(!isRunning) {
                Integer retry = taskRetry.get(tmpGoods.getId());
                retry = retry == null ? new Integer(1) : retry.intValue() + 1;
                taskRetry.put(tmpGoods.getId(), retry);
                if (retry.intValue() > maxRetry.intValue()) {
                    // 재시도 횟수가 없을 경우
                    String message = String.format(stopMessage, tmpGoods.getName(), retry.intValue());
                    telegramHandler.adminSend(origin, message);
                    continue;
                }
                // 재시도 횟수가 남은 상태
                String message = String.format(retryMessage, tmpGoods.getName(), retry.intValue(), maxRetry.intValue());
                telegramHandler.adminSend(origin, message);
                taskService.liveTaskRun(traderTask, true);
            }
        }
    }



}