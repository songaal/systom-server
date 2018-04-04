package io.gncloud.coin;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ReadKinesisStreamSample {

    private static Logger logger = LoggerFactory.getLogger(ReadKinesisStreamSample.class);

    private static ProfileCredentialsProvider credentialsProvider;

    public static void main(String[] args) {
        String region = "ap-northeast-2";
        String streamName = "order_stream";
        String shardId = "shard-000000000000";
        int limit = 1000;
        ReadKinesisStreamSample sample = new ReadKinesisStreamSample();
        sample.read(region, streamName, shardId, limit);
    }

    private void init() {
        credentialsProvider = new ProfileCredentialsProvider();
        try {
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
                    + "Please make sure that your credentials file is at the correct "
                    + "location (~/.aws/credentials), and is in valid format.", e);
        }
    }

    /**
     * iteratorType : https://docs.aws.amazon.com/ko_kr/kinesis/latest/APIReference/API_GetShardIterator.html
     *              : AFTER_SEQUENCE_NUMBER, LATEST, TRIM_HORIZON, AT_SEQUENCE_NUMBER, AT_TIMESTAMP
     * limit 의 최대갯수는 10,000
     */
    private void read(String region, String streamName, String shardId, int limit) {

        AmazonKinesis client = AmazonKinesisClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(region)
                .build();


        //recordSequenceNumber 를 저장소에서 로드한다.
        String recordSequenceNumber = loadLastSequenceNumber(streamName, shardId);

        GetShardIteratorRequest getShardIteratorRequest = new GetShardIteratorRequest();
        getShardIteratorRequest.setStreamName(streamName);
        getShardIteratorRequest.setShardId(shardId);

        if(recordSequenceNumber != null) {
            getShardIteratorRequest.setShardIteratorType("AFTER_SEQUENCE_NUMBER");
            getShardIteratorRequest.setStartingSequenceNumber(recordSequenceNumber);
        } else {
            getShardIteratorRequest.setShardIteratorType("TRIM_HORIZON");
        }

        GetShardIteratorResult getShardIteratorResult = client.getShardIterator(getShardIteratorRequest);
        String shardIterator = getShardIteratorResult.getShardIterator();

        while(shardIterator != null) {
            GetRecordsRequest getRecordsRequest = new GetRecordsRequest();
            getRecordsRequest.withShardIterator(shardIterator);
            getRecordsRequest.setLimit(limit);

            GetRecordsResult getRecordsResult = client.getRecords(getRecordsRequest);
            List<Record> records = getRecordsResult.getRecords();

            for (Record record : records) {
                logger.info("########### {}", record);
                recordSequenceNumber = record.getSequenceNumber();
                //websocket으로 보낸다.
                sendToWebsocketData(record);
                //db에 입력한다.
                saveToDatabase(record);
            }

            if (records.size() > 0) {
                shardIterator = getRecordsResult.getNextShardIterator();
                //서버 다운에 대비해 recordSequenceNumber 를 저장해놓는다.
                saveLastSequenceNumber(streamName, shardId, recordSequenceNumber);
                logger.info("## Save sequence [{}]", recordSequenceNumber);
            } else {
                shardIterator = null;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //ignore
            }

        }
    }

    private void saveToDatabase(Record record) {
        record.getData();
    }

    private void sendToWebsocketData(Record record) {

    }

    private void saveLastSequenceNumber(String streamName, String shardId, String sequenceNumber) {

    }


    private String loadLastSequenceNumber(String streamName, String shardId) {

//        "49583187797881247211893756636239162007126231214234533890"
        return null;
    }

}
