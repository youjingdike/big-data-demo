package com.xq.nio.selector;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SelectSocket {
    public static int PORT_NUMBER = 1234;

    public static void main(String[] args) throws Exception {
        new SelectSocket().go(args);
    }

    public void go(String[] args) throws Exception {
        int port = PORT_NUMBER;
        if (args.length>0) {
            port = Integer.parseInt(args[0]);
        }
        System.out.println("Listening on port "+port);
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ServerSocket serverSocket = serverSocketChannel.socket();
        Selector selector = Selector.open();
        serverSocket.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        registerChannel(selector,serverSocketChannel,SelectionKey.OP_ACCEPT);

        while (true) {
            int n = selector.select();
            if (n==0) {
                continue;
            }
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel)key.channel();
                    SocketChannel accept = server.accept();
                    registerChannel(selector,accept,SelectionKey.OP_READ);
                    sayHello(accept);
                }
                if (key.isReadable()) {
                    readDataFromSocket(key);
                }
                it.remove();
            }
        }
    }

    protected void registerChannel(Selector selector, SelectableChannel channel,int ops)  throws  Exception{
        if (channel==null) {
            return;
        }
        channel.configureBlocking(false);
        channel.register(selector, ops);
    }

    private ByteBuffer buffer = ByteBuffer.allocate(1024);

    protected void readDataFromSocket(SelectionKey key) throws Exception {
        SocketChannel socketChannel = (SocketChannel)key.channel();
        int count;
        buffer.clear();
        while ((count = socketChannel.read(buffer)) > 0) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                socketChannel.write(buffer);
            }
            //WARNING:the above loop is evil.
            buffer.clear();
        }
        if (count < 0) {
            socketChannel.close();
        }
    }

    private void sayHello(SocketChannel channel) throws Exception {
        buffer.clear();
        buffer.put("Hi there!\r\n".getBytes());
        buffer.flip();
        channel.write(buffer);
    }
}
