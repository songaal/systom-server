package io.gncloud.coin.server.service;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.*;
import com.google.gson.Gson;
import io.gncloud.coin.server.model.EventMetadata;
import io.gncloud.coin.server.ws.EventWebSocketHandler;
import io.gncloud.coin.server.ws.WebSocketSessionInfo;
import io.gncloud.coin.server.ws.WebSocketSessionInfoSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class EventService {

    private static Logger logger = LoggerFactory.getLogger(EventService.class);

    @Autowired
    private IdentityService identityService;

    @Value("${aws.stream.region}")
    private String region = "ap-northeast-2";
    @Value("${aws.stream.name}")
    private String streamName = "order_stream";
    @Value("${aws.stream.shardId}")
    private String shardId = "shard-000000000000";
    @Value("${aws.stream.limit}")
    private int limit = 1000;

    private AmazonKinesis client;
    private String shardIterator;
    private String recordSequenceNumber;

    private Map<String, WebSocketSessionInfoSet> websocketSubscriberMap;

    private Gson gson = new Gson();

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
                    //websocket으로 보낸다.
                    sendToWebsocketData(record);
                    //db에 입력한다.
                    saveToDatabase(record);
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

    private void saveToDatabase(Record record) {
        //TODO
        ByteBuffer byteBuffer = record.getData();
        logger.debug("##### data > {}", new String(byteBuffer.array()));
    }

    private void sendToWebsocketData(Record record) {
        //TODO

        String key = null;
        String jsonData = new String(record.getData().array());

        EventMetadata eventMetadata = gson.fromJson(jsonData, EventMetadata.class);
        if(eventMetadata.getArena().equals("backtest")) {
            key = EventWebSocketHandler.KEY_PREFIX_BACKTEST + eventMetadata.getUser() + "_" + eventMetadata.getStarategyId() + "_" + eventMetadata.getTestId();
        } else {
            key = EventWebSocketHandler.KEY_PREFIX_AGENT + eventMetadata.getUser() + "_" + eventMetadata.getAgentId();
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
