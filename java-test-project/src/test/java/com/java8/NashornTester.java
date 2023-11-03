package com.java8;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class NashornTester {
	
	@Test
	public void test() {
		final String text = "Base64 finally in Java 8! 你好";
        
        final String encodedStr = Base64
            .getEncoder()
            .encodeToString( text.getBytes( StandardCharsets.UTF_8 ) );
        System.out.println( encodedStr );
         
        final String decoded = new String( 
            Base64.getDecoder().decode( encodedStr ),
            StandardCharsets.UTF_8 );
        System.out.println( decoded );

        //Base64类同时还提供了对URL、MIME友好的编码器与解码器（Base64.getUrlEncoder() / Base64.getUrlDecoder(), Base64.getMimeEncoder() / Base64.getMimeDecoder()）
		
	}
	
	
}

