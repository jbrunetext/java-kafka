package com.github.brunetj.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;


public class ConsumerDemoAssignSeek {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(ConsumerDemoAssignSeek.class.getName());
        Properties properties = new Properties();
        String bootstrapServers = "127.0.0.1:9092";
        String groupId= "app0";
        String topic= "new_topic";
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");

        KafkaConsumer<String ,String> consumer = new KafkaConsumer<String, String>(properties);
        //consumer.subscribe(Arrays.asList(topic));

        TopicPartition partitionToreadFrom = new TopicPartition(topic,0);
        long offsetToreadFrom = 15L;

        //consumer.assign
        consumer.assign(Arrays.asList(partitionToreadFrom));

        //consumer.seek
        consumer.seek(partitionToreadFrom,offsetToreadFrom);
        int numberOfMessagesToRead = 5;
        boolean breakOnReading = true;
        int numberOfMessagesReadSoFar = 0;

        while (breakOnReading)
        {
            ConsumerRecords<String,String> records =
                    consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record: records)
            {
                numberOfMessagesReadSoFar +=1;
                logger.info(" key: "+ record.key()+ " value: "+record.value());
                logger.info(" partition: "+ record.partition()+ " Offset: "+record.offset());
                if (numberOfMessagesReadSoFar >= numberOfMessagesToRead )
                {
                    breakOnReading=false;
                    break;
                }

            }
        }
       /// KafkaConsumer
    }
}
