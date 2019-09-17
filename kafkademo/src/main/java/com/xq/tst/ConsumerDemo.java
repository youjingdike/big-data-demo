package com.xq.tst;

import com.google.common.collect.Lists;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConsumerDemo {
    private void kafkaHandler() {
        Properties consumerConf = getConsumerConf();
        final Map<TopicPartition, OffsetAndMetadata> currentOffset = new HashMap<>(32);
        final KafkaConsumer<String,String> kafkaConsumer = new KafkaConsumer(consumerConf);

        class HandlerRebalance implements ConsumerRebalanceListener {

            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> collection) {
                kafkaConsumer.commitSync(currentOffset);
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> collection) {

            }
        }

        kafkaConsumer.subscribe(Lists.newArrayList("testTopic"),new HandlerRebalance());
        for (;;) {
            ConsumerRecords<String,String> records = kafkaConsumer.poll(3000);
            records.forEach(s->{
                System.out.println(s.value());
                currentOffset.put(new TopicPartition(s.topic(),s.partition()),new OffsetAndMetadata(s.offset()+1,"no metadata"));
            });
            kafkaConsumer.commitSync(currentOffset);
        }
    }

    private Properties getConsumerConf() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "xxx:9092");
        properties.put("group.id", "client-dev");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("enable.auto.commit", "false");
        properties.put("auto.offset.reset", "latest");
        return properties;
    }
}
