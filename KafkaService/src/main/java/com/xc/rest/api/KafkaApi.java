package com.xc.rest.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(value = "/api/v1")
public interface KafkaApi {
	
	@RequestMapping(value="/kafka/sendMessage",method = RequestMethod.GET)
	public String sendMessage(@RequestParam(value = "topic") String topic, @RequestParam(value = "msg") String msg);
	

}
