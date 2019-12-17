package com.xq.nio;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * test copying between channels!
 *
 */
public class ChannelCopy {

    public static void main( String[] args ) throws IOException {
        ReadableByteChannel src = Channels.newChannel(System.in);
        WritableByteChannel dest = Channels.newChannel(System.out);
        channelCopy1(src,dest);
//        channelCopy2(src,dest);
        src.close();
        dest.close();

    }

    private static void channelCopy1(ReadableByteChannel src,WritableByteChannel dest) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(16 * 1024);
        while (src.read(byteBuffer) != -1) {
            byteBuffer.flip();
            dest.write(byteBuffer);
            byteBuffer.compact();
        }

        byteBuffer.flip();
        while (byteBuffer.hasRemaining()) {
            dest.write(byteBuffer);
        }
    }

    private static void channelCopy2(ReadableByteChannel src,WritableByteChannel dest) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(16 * 1024);
        while (src.read(byteBuffer) != -1) {
            byteBuffer.flip();
            while (byteBuffer.hasRemaining()) {
                dest.write(byteBuffer);
            }
            byteBuffer.clear();
        }
    }


}
