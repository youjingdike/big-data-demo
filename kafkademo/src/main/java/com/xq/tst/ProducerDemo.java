package com.xq.tst;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class ProducerDemo {
    public static void putData() {
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(getProps());
        kafkaProducer.send(new ProducerRecord<String,String>("testTopic",null,"test"));
    }
    private static Properties getProps() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "xxx:9092");
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("batch.size", 16384);
        properties.put("linger.ms", 1);
        properties.put("buffer.memory", 33554432);
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return properties;
    }
}
