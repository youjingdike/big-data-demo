package com.xc.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.xc.kafka.KafkaProducer;
import com.xc.rest.api.KafkaApi;

@RestController
public class KafkaServer implements KafkaApi {
	
	@Autowired
	private KafkaProducer kafkaProducer;

    /**
     *
     * @param topic
     * @param msg
     * @return
     */
	@Override
	public String sendMessage(String topic, String msg) {
		kafkaProducer.sendMessage("kettle_info", msg);
		return "sucess";
	}

}
