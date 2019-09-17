package com.xc.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @SuppressWarnings("rawtypes")
	@Autowired
    private KafkaTemplate kafkaTemplate;

    @SuppressWarnings("unchecked")
	public void sendMessage(String topic, String message) {
        logger.info("on message:{}", message);
        kafkaTemplate.send(topic,message);
    }
}
