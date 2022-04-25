package com.websocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class SocketClient extends WebSocketClient{

	public SocketClient(URI serverUri, Draft draft) {
		super(serverUri, draft);
	}

	@Override
	public void onClose(int arg0, String arg1, boolean arg2) {
		
		
	}

	@Override
	public void onError(Exception arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(String notification) {
		
		 System.out.println(notification);
		
	}

	@Override
	public void onOpen(ServerHandshake arg0) {
		send("rfnconnect#omsn#ddddddd");
	}
}