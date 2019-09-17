/** 
 *太极云软件技术股份有限公司版权所有 1990-2016. http://www.tyky.com.cn
 * @file springBootExample.example.infrastructure
 * 
 */
package com.xc.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class SendEmail {

	 	@Autowired
	    private JavaMailSender mailSender; 

	    @Value("${spring.mail.username}")
	    private String Sender; 

	    
	    public void sendSimpleMail(String[] emailAdd,String messageInfo) throws Exception {
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setFrom(Sender);
	        message.setTo(emailAdd);
	        message.setSubject("系统预警！");
	        message.setText(messageInfo);
	        mailSender.send(message);
	    }

}
