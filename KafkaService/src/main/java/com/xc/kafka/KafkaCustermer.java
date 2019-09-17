package com.xc.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.xc.utils.SendEmail;

@Component
public class KafkaCustermer {
	
	@Autowired
	SendEmail sendEmail;

    @KafkaListener(topics = {"kettle_info"})
    public void consumer(ConsumerRecord<?, ?> cr) {
  	System.out.println(cr.value());
  	
  		try {
  			String[] emailAdd=new String[3];
  			emailAdd[0]="88477025@qq.com";
  			emailAdd[1]="578979985@qq.com";

  			
			sendEmail.sendSimpleMail(emailAdd,cr.value().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

  	
  }


}
