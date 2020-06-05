package com.xq.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

public class Test {
    public static void main(String[] args) {
        String str = "bbbbbbbbbbbbb";
        ByteBuf buffer = Unpooled.buffer(str.length());
        buffer.writeBytes(str.getBytes());
        System.out.println(new String(buffer.array()));
        buffer.setByte(2,(byte)'a');
        buffer.setByte(4,(byte)'a');
        buffer.setByte(6,(byte)'a');
        System.out.println(new String(buffer.array()));
    }
}
