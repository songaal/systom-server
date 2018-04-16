package io.gncloud.coin.server.service;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.*;
import com.google.gson.Gson;
import io.gncloud.coin.server.model.EventMetadata;
import io.gncloud.coin.server.model.Order;
import io.gncloud.coin.server.ws.EventWebSocketHandler;
import io.gncloud.coin.server.ws.WebSocketSessionInfoSet;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Service
public class EventService {

    private static Logger logger = LoggerFactory.getLogger(EventService.class);

    @Autowired
    private IdentityService identityService;

    @Value("${aws.stream.region}")
    private String region;
    @Value("${aws.stream.name}")
    private String streamName;
    @Value("${aws.stream.shardId}")
    private String shardId;
    @Value("${aws.stream.limit}")
    private int limit;

    private AmazonKinesis client;
    private String shardIterator;
    private String recordSequenceNumber;

    private Map<String, WebSocketSessionInfoSet> websocketSubscriberMap;

    private Gson gson = new Gson();

    @Autowired
    private SqlSession sqlSession;

    @PostConstruct
    public void init() {
        client = AmazonKinesisClientBuilder.standard()
                .withRegion(region)
                .build();


        //recordSequenceNumber 를 저장소에서 로드한다.
        String recordSequenceNumber = loadLastSequenceNumber(streamName, shardId);

        shardIterator = getShardIterator(streamName, shardId, client, recordSequenceNumber);

        websocketSubscriberMap = identityService.getSubscriberMap();
    }

    String lastShardIterator = null;
    @Scheduled(fixedDelay= 1000)
    public void scheduled() {
        try {
            if(shardIterator != lastShardIterator) {
                logger.debug("#### check record by shardIterator : {}", shardIterator);
            }
            lastShardIterator = shardIterator;

            GetRecordsRequest getRecordsRequest = new GetRecordsRequest();
            getRecordsRequest.setShardIterator(shardIterator);
            getRecordsRequest.setLimit(limit);

            GetRecordsResult getRecordsResult = client.getRecords(getRecordsRequest);

            List<Record> records = getRecordsResult.getRecords();

            if (records.size() > 0) {

                for (Record record : records) {
                    logger.info("########### {}", record);
                    recordSequenceNumber = record.getSequenceNumber();

                    String jsonData = new String(record.getData().array());
                    EventMetadata eventMetadata = gson.fromJson(jsonData, EventMetadata.class);

                    //backtest 종료 이벤트 받기.
                    if(eventMetadata.isFinished()) {
                        //finished 이벤트는 backtest에만 존재하지만 한번 더 확인.
                        if(eventMetadata.isBackTestMode()) {
                            //TODO 구현
                            //summary = calculateFinalProfit(record);
                            //saveToDatabase(summary);
                        }
                    } else {
                        //websocket으로 보낸다.
                        sendToWebsocketData(jsonData, eventMetadata);

                        //라이브나 페이퍼모드는 중간중간 오더를 모두 기록한다.
                        if(eventMetadata.isLiveMode() || eventMetadata.isPaperMode()) {
                            //TODO 구현
                            //summary = calculateRunningProfit(record);
                            List<Order> orders = eventMetadata.getOrders();
                            if (orders != null && orders.size() > 0) {
                                saveToDatabase(eventMetadata.getAgentId(), orders);
                            }
                        }
                    }
                }

                shardIterator = getRecordsResult.getNextShardIterator();
                //서버 다운에 대비해 recordSequenceNumber 를 저장해놓는다.
                saveLastSequenceNumber(streamName, shardId, recordSequenceNumber);
                logger.info("## Save sequence [{}]", recordSequenceNumber);
            } else {
                //최신 데이터 없음.
            }

        } catch (ExpiredIteratorException e) {
            logger.debug("iterator expired. create again: {}", e.getMessage());
            shardIterator = getShardIterator(streamName, shardId, client, recordSequenceNumber);
        } catch (Throwable t) {
            logger.error("", t);
        }
    }

    private String getShardIterator(String streamName, String shardId, AmazonKinesis client, String recordSequenceNumber) {
        GetShardIteratorRequest getShardIteratorRequest = new GetShardIteratorRequest();
        getShardIteratorRequest.setStreamName(streamName);
        getShardIteratorRequest.setShardId(shardId);

        if (recordSequenceNumber != null) {
            getShardIteratorRequest.setShardIteratorType("AFTER_SEQUENCE_NUMBER");
            getShardIteratorRequest.setStartingSequenceNumber(recordSequenceNumber);
        } else {
            getShardIteratorRequest.setShardIteratorType("LATEST");
        }

        GetShardIteratorResult getShardIteratorResult = client.getShardIterator(getShardIteratorRequest);
        return getShardIteratorResult.getShardIterator();
    }

    private void saveToDatabase(Integer agentId, List<Order> orders) {
//        ByteBuffer byteBuffer = record.getData();
//        logger.debug("##### data > {}", new String(byteBuffer.array()));
        orders.forEach(order -> {
            logger.debug("Insert Order", order);
            try {
                order.setAgentId(agentId);
                order.setTimestamp(order.getTimestamp() / 1000000000);
                sqlSession.insert("order.insertOrder", order);
            } catch (Exception e) {
                logger.debug("order: {}", order);
                logger.error("[FAIL] Insert Order Error", e);
            }
        });
    }

    private void sendToWebsocketData(String jsonData, EventMetadata eventMetadata) {
        String key = null;
        if(eventMetadata.isBackTestMode()) {
            key = EventWebSocketHandler.KEY_PREFIX_BACKTEST + eventMetadata.getTestId();
        } else {
            key = EventWebSocketHandler.KEY_PREFIX_AGENT + eventMetadata.getAgentId();
        }
        TextMessage message = new TextMessage(jsonData);
        WebSocketSessionInfoSet infoSet = websocketSubscriberMap.get(key);

        if(infoSet != null) {
            infoSet.sendTextMessage(message);
        } else {
            logger.debug("New record found, but websocket is not connected. {}", key);
        }
    }

    private void saveLastSequenceNumber(String streamName, String shardId, String sequenceNumber) {
        //TODO
    }

    private String loadLastSequenceNumber(String streamName, String shardId) {
        //TODO
        return null;
    }
}
