/*
package com.xq.tst;

import com.google.common.collect.Lists;
import kafka.common.TopicAndPartition;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConsumerDemo {
    public static void main(String[] args) {
        ConsumerDemo consumerDemo = new ConsumerDemo();
        consumerDemo.kafkaHandler();
    }
    private void kafkaHandler() {
        Properties consumerConf = getConsumerConf();
        final Map<TopicPartition, OffsetAndMetadata> currentOffset = new HashMap<>(32);
        final KafkaConsumer<String,String> kafkaConsumer = new KafkaConsumer(consumerConf);
        final ArrayList<String> topics = Lists.newArrayList("upsert-tst");
        String groupId = consumerConf.getProperty("group.id");
        String brokerList = consumerConf.getProperty("bootstrap.servers");
        String reSet = consumerConf.getProperty("auto.offset.reset");

        class HandlerRebalance implements ConsumerRebalanceListener {

            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> collection) {
                kafkaConsumer.commitSync(currentOffset);
                currentOffset.clear();
                initOffset(collection);
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> collection) {
                initOffset(collection);
            }

            private void initOffset(Collection<TopicPartition> collection) {
                String clientId = "offsetLookup" + System.currentTimeMillis();
                Map<TopicAndPartition, Long> consumerGroupOffset = KafkaInfoTool.getInstance().getConsumerGroupOffset(groupId, brokerList, topics, clientId);
                Map<TopicAndPartition,Long> offset = null;

                if ("latest".equals(reSet)) {
                    offset = KafkaInfoTool.getInstance().getLastOffset(brokerList, topics, clientId);
                } else {
                    offset = KafkaInfoTool.getInstance().getEarliestOffset(brokerList, topics, clientId);
                }
                Map<TopicAndPartition, Long> finalOffset = offset;
                collection.forEach(tp->{
                    TopicAndPartition key = new TopicAndPartition(tp.topic(), tp.partition());
                    Long offsetInit = consumerGroupOffset.get(key);
                    if (offsetInit == -1) {
                        offsetInit = finalOffset.get(key);
                    }
                    if (offsetInit != null) {
                        currentOffset.put(new TopicPartition(tp.topic(), tp.partition()), new OffsetAndMetadata(offsetInit, "no metadata"));
                    }
                });
            }
        }


        kafkaConsumer.subscribe(topics,new HandlerRebalance());
        for (;;) {
            ConsumerRecords<String,String> records = kafkaConsumer.poll(3000);
            records.forEach(s->{
                System.out.println(s.toString());
//                System.out.println(s.value());
                currentOffset.put(new TopicPartition(s.topic(),s.partition()),new OffsetAndMetadata(s.offset()+1,"no metadata"));
            });
            kafkaConsumer.commitSync(currentOffset);
        }
    }

    private Properties getConsumerConf() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "192.168.80.101:9092,192.168.80.102:9092,192.168.80.103:9092");
        properties.put("group.id", "client-dev2");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("enable.auto.commit", "false");
        properties.put("auto.offset.reset", "earliest");
        return properties;
    }
}
*/
