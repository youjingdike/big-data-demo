package com.xq.nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ChannelAccept {
    public static final String GREETING = "Hello I must be going.\r\n";

    public static void main(String[] args) throws Exception {
        int port = 1234;
        if (args.length>0) {
            port = Integer.parseInt(args[0]);
        }
        ByteBuffer buffer = ByteBuffer.wrap(GREETING.getBytes());
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ServerSocket socket = ssc.socket();
        socket.bind(new InetSocketAddress(port));
        ssc.configureBlocking(false);
        while (true) {
            System.out.println("Waiting for connections");
            SocketChannel sc = ssc.accept();
            if (sc == null) {
                Thread.sleep(2000);
            } else {
                System.out.println("Incoming connection from: " +
                        sc.socket().getRemoteSocketAddress());
                buffer.rewind();
                sc.write(buffer);
                sc.close();
            }
        }
    }

}
