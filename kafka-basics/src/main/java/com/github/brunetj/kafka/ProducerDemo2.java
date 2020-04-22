package com.github.brunetj.kafka;

import com.github.brunetj.kafka.ProducerDemo;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemo2 {
    private static Logger logger = LoggerFactory.getLogger(ProducerDemo.class);
    public static void main(String[] args) {

        String bootstrapServers = "127.0.0.1:9092";
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //create the producer
        KafkaProducer<String,String> producer = new KafkaProducer<String, String>(properties);
        for(int i=0; i<10; i++){
            String value = Integer.toString(i);
            String key = "id_" + Integer.toString(i);
            ProducerRecord<String, String> record= new ProducerRecord<String, String>("new_topic", key, value);
            //send data..asynchronous
            logger.info("key :",key);
            producer.send(record, new Callback() {
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (e == null)
                        logger.info("received new metadata .\n"+
                                "Topic name : " + recordMetadata.topic() + "\n" +
                                "Topic offsett : " + recordMetadata.offset() + "\n" +
                                "Topic key : " + key + "\n" );
                    else
                    {
                        logger.error("Error when publish ",e);
                    }
                }
            });

        }
        producer.close();


        System.out.println("End Producer demo 2");
    }
}
