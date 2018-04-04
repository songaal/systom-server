package io.gncloud.coin;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
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
//        sample.write(region, streamName);
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

    private void write(String region, String streamName) {
        AmazonKinesis client = AmazonKinesisClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(region)
                .build();
        try {
            for (int i = 0; i < 10; i++) {
                String partitionKey = Long.toString(System.nanoTime());


                String data = "{\n" +
                        "    \"records\": [\n" +
                        "        {\n" +
                        "            \"partition-key\": \"87653456789876458\",\n" +
                        "            \"data\": {\n" +
                        "                \"seq\": \"" + i + "\",\n" +
                        "                \"value\": {\n" +
                        "                    \"ColumnNames\": [\n" +
                        "                        \"userId\",\n" +
                        "                        \"documentId\",\n" +
                        "                        \"Scored Labels\",\n" +
                        "                        \"Scored Probabilities\"\n" +
                        "                    ],\n" +
                        "                    \"ColumnTypes\": [\n" +
                        "                        \"String\",\n" +
                        "                        \"String\",\n" +
                        "                        \"Boolean\",\n" +
                        "                        \"Double\"\n" +
                        "                    ],\n" +
                        "                    \"Values\": [\n" +
                        "                        [\n" +
                        "                            \"100213199594809000000\",\n" +
                        "                            \"1Ktol-SWvAh8pnHG2O7HdPrfbEVZWX3Vf2YIPYXA_8gI\",\n" +
                        "                            \"False\",\n" +
                        "                            \"0.375048756599426\"\n" +
                        "                        ],\n" +
                        "                        [\n" +
                        "                            \"103097844766994000000\",\n" +
                        "                            \"1jYsTPJH8gaIiATix9x34Ekcj31ifJMkPNb0RmxnuGxs\",\n" +
                        "                            \"True\",\n" +
                        "                            \"0.753859758377075\"\n" +
                        "                        ]\n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            }\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}";
                byte[] bytes = data.getBytes("utf-8");
                ByteBuffer buffer = ByteBuffer.wrap(bytes);
                client.putRecord(streamName, buffer, partitionKey);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * iteratorType : https://docs.aws.amazon.com/ko_kr/kinesis/latest/APIReference/API_GetShardIterator.html
     * : AFTER_SEQUENCE_NUMBER, LATEST, TRIM_HORIZON, AT_SEQUENCE_NUMBER, AT_TIMESTAMP
     * limit 의 최대갯수는 10,000
     */
    private void read(String region, String streamName, String shardId, int limit) {

        AmazonKinesis client = AmazonKinesisClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(region)
                .build();


        //recordSequenceNumber 를 저장소에서 로드한다.
        String recordSequenceNumber = loadLastSequenceNumber(streamName, shardId);

        String shardIterator = getShardIterator(streamName, shardId, client, recordSequenceNumber);

        while (true) {
            try {
                logger.debug("#### shardIterator : {}", shardIterator);
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
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                //ignore
            }

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
