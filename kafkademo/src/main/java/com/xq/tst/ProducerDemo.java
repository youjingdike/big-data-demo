package com.xq.tst;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ProducerDemo {
    public static void putData() {
        int num = 1000000;
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(getProps());
        try {
            for (int i = 0; i < num; i++) {
                String value = "bbbbbbb:"+i;
                kafkaProducer.send(new ProducerRecord<String,String>("xqtest",null,value)).get();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    private static Properties getProps() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "node1.hadoop.com:9092");
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("batch.size", 16384);
        properties.put("linger.ms", 1);
        properties.put("buffer.memory", 33554432);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return properties;
    }

    public static void main(String[] args) {
        putData();
    }
}
