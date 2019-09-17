package com.xc.KafkaService;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Properties;

public class Test {
    Properties properties = new Properties();
    KafkaConsumer kafkaConsumer = new KafkaConsumer(properties);
}
