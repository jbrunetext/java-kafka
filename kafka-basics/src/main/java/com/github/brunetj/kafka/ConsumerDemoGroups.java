package com.github.brunetj.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;


public class ConsumerDemoGroups {
    public static void main(String[] args) {
        new ConsumerDemoGroups().run();
    }
    private ConsumerDemoGroups()
    {


    }
    private void run()
    {
        Logger logger = LoggerFactory.getLogger(ConsumerDemoGroups.class.getName());
        String bootstrapServers = "127.0.0.1:9092";
        String groupId = "app3";
        String topic = "new_topic";

        CountDownLatch latch = new CountDownLatch(1);
        logger.info("Create myConsumerThread");
        Runnable myConsumerThread = new ConsumerThread(bootstrapServers,groupId,topic,latch);
        Thread myThread = new Thread(myConsumerThread);
        myThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            logger.info("Caught  Shutdown Hook");
            ((ConsumerThread) myConsumerThread).shutdown();
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("Application has exited");
        }));
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class ConsumerThread implements Runnable {
        private KafkaConsumer<String, String> consumer;
        private CountDownLatch latch;

        public ConsumerThread(String bootstrapServers, String groupId, String topic, CountDownLatch latch) {
            this.latch = latch;
            Properties properties = new Properties();
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            consumer = new KafkaConsumer<String, String>(properties);
            consumer.subscribe(Arrays.asList((topic)));
        }

        @Override
       public void run() {

            Logger logger = LoggerFactory.getLogger(ConsumerThread.class.getName());

            logger.info("Creating the consumer Thread");

            try {
                while (true) {
                    ConsumerRecords<String, String> records =
                            consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, String> record : records) {
                        logger.info(" key: " + record.key() + " value: " + record.value());
                        logger.info(" partition: " + record.partition() + " Offset: " + record.offset());
                    }
                }

            } catch (WakeupException e)
            {
               logger.info("Received Shutdown Signal");
            }finally {
                consumer.close();
                //tell our main code we 're done with the consumer.
                logger.info("We close the consumer");
                latch.countDown();
            }
        }

        public void shutdown() {

            //te wakeu() methid is a special methid to interrupt consumer.poll
            //and throw execption
            consumer.wakeup();
        }
    }
}
