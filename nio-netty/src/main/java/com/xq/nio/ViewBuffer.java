package com.xq.nio;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;

/**
 * Hello world!
 *
 */
public class ViewBuffer {

    public static void main( String[] args ) {

        ByteBuffer byteBuffer = ByteBuffer.allocate(7).order(ByteOrder.BIG_ENDIAN);
        CharBuffer charBuffer = byteBuffer.asCharBuffer();//创建bytebuffer缓冲区的charbuffer视图

        byteBuffer.put(0, (byte) 0);
        byteBuffer.put(1, (byte) 'H');
        byteBuffer.put(2, (byte) 0);
        byteBuffer.put(3, (byte) 'i');
        byteBuffer.put(4, (byte) 0);
        byteBuffer.put(5, (byte) '!');
        byteBuffer.put(6, (byte) 0);
        println(byteBuffer);
        println(charBuffer);
        System.out.println("你好".getBytes().length);
        System.out.println("a".getBytes().length);
    }

    private static void println(Buffer buffer) {
        System.out.println("pos="+buffer.position()+",limit="+buffer.limit()
            +",capacity="+buffer.capacity()
            +":'"+buffer.toString()+"'");
    }
}
