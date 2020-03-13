package com.xq.nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class ConnectAsyc {
    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int port = 80;
        if (args.length == 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }
        InetSocketAddress addr = new InetSocketAddress(host, port);
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        System.out.println("initiating connection");
        sc.connect(addr);
        while (! sc.finishConnect()) {
            doSomethingUseful();
        }
        System.out.println("connection established");
        //Do something with the connected socket
        //The SocketChannel is still nonblocking
        sc.close();
    }

    private static void doSomethingUseful() {
        System.out.println("do something useless");
    }
}
