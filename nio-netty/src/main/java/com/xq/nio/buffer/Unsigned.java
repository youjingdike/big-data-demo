package com.xq.nio.buffer;

import java.nio.ByteBuffer;

public class Unsigned {
    public static short getUnsignedByte(ByteBuffer byteBuffer) {
        return (short)(byteBuffer.get() & 0xff);
    }

    public static void putUnsignedByte(ByteBuffer byteBuffer,int value) {
        byteBuffer.put((byte) (value & 0xff));
    }

    public static short getUnsignedByte(ByteBuffer byteBuffer,int position) {
        return (short)((byteBuffer.get(position) & (short)0xff));
    }

    public static void putUnsignedByte(ByteBuffer byteBuffer,int position,int value) {
        byteBuffer.put(position,(byte) (value & 0xff));
    }

    //-------------------------------------------------------------------------
    public static int getUnsignedShort(ByteBuffer byteBuffer) {
        return (short)(byteBuffer.getShort() & 0xffff);
    }

    public static void putUnsignedShort(ByteBuffer byteBuffer,int value) {
        byteBuffer.putShort((short) (value & 0xffff));
    }

    public static int getUnsignedShort(ByteBuffer byteBuffer,int position) {
        return byteBuffer.getShort(position) & 0xffff;
    }

    public static void putUnsignedShort(ByteBuffer byteBuffer,int position,int value) {
        byteBuffer.putShort(position,(short) (value & 0xffff));
    }

    //-------------------------------------------------------------------------
    public static long getUnsignedInt(ByteBuffer byteBuffer) {
        return (long)(byteBuffer.getInt() & 0xffffffffL);
    }

    public static void putUnsignedInt(ByteBuffer byteBuffer,int value) {
        byteBuffer.putInt((int) (value & 0xffffffffL));
    }

    public static long getUnsignedInt(ByteBuffer byteBuffer,int position) {
        return (long)byteBuffer.getInt(position) & 0xffffffffL;
    }

    public static void putUnsignedInt(ByteBuffer byteBuffer,int position,int value) {
        byteBuffer.putInt(position,(int) (value & 0xffffffffL));
    }
}
