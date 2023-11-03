package com.websocket;

import org.java_websocket.drafts.Draft_17;

import java.net.URI;
import java.net.URISyntaxException;

public class Test {
	public static void main(String[] args) {
		SocketClient sc =  null;
			try {
				sc = new SocketClient(new URI("ws://127.0.0.1:8080/notify/websck"), new Draft_17());
				sc.connect();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
	}
}
