package com.xq.nio;

import java.nio.ByteBuffer;

public class Unsigned {
    public static short getUnsignedByte(ByteBuffer byteBuffer) {
        return (short)(byteBuffer.get() & 0xff);
    }

    public static void putUnsignedByte(ByteBuffer byteBuffer,int value) {
        byteBuffer.put((byte) (value & 0xff));
    }

    public static short getUnsignedByte(ByteBuffer byteBuffer,int postition) {
        return (short)((byteBuffer.get(postition) & (short)0xff));
    }

    public static void putUnsignedByte(ByteBuffer byteBuffer,int postition,int value) {
        byteBuffer.put(postition,(byte) (value & 0xff));
    }

    //-------------------------------------------------------------------------
    public static int getUnsignedShort(ByteBuffer byteBuffer) {
        return (short)(byteBuffer.getShort() & 0xffff);
    }

    public static void putUnsignedShort(ByteBuffer byteBuffer,int value) {
        byteBuffer.putShort((short) (value & 0xffff));
    }

    public static int getUnsignedShort(ByteBuffer byteBuffer,int postition) {
        return byteBuffer.getShort(postition) & 0xffff;
    }

    public static void putUnsignedShort(ByteBuffer byteBuffer,int postition,int value) {
        byteBuffer.putShort(postition,(short) (value & 0xffff));
    }

    //-------------------------------------------------------------------------
    public static long getUnsignedInt(ByteBuffer byteBuffer) {
        return (long)(byteBuffer.getInt() & 0xffffffffL);
    }

    public static void putUnsignedInt(ByteBuffer byteBuffer,int value) {
        byteBuffer.putInt((int) (value & 0xffffffffL));
    }

    public static long getUnsignedInt(ByteBuffer byteBuffer,int postition) {
        return (long)byteBuffer.getInt(postition) & 0xffffffffL;
    }

    public static void putUnsignedInt(ByteBuffer byteBuffer,int postition,int value) {
        byteBuffer.putInt(postition,(int) (value & 0xffffffffL));
    }
}
