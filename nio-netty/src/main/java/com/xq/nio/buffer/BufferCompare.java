package com.xq.nio.buffer;

import java.nio.CharBuffer;

/**
 * Hello world!
 *
 */
public class BufferCompare {

    private static void drainBuffer(CharBuffer charBuffer) {
        while (charBuffer.hasRemaining()) {
            System.out.println(charBuffer.get());
        }
        System.out.println("@@@@@@@@@@@@@");
    }

    private static void drainBuffer(CharBuffer charBuffer,int index) {
        int remaining = charBuffer.remaining();//该方法会告诉你从当前位置到上界还剩余的元素个数
        int num = remaining>=index?index:remaining;
        for (int i=0;i<num;i++) {
            System.out.println(charBuffer.get());
        }
        System.out.println("@@@@@@@@@@@@@");
    }

    private static boolean fillBuffer(CharBuffer charBuffer,String str) {
        if (str == null) {
            return false;
        }
        for (int i=0; i<str.length();i++) {
            charBuffer.put(str.charAt(i));
        }
        return true;
    }

    public static void main( String[] args ) {
        CharBuffer charBuffer1 = CharBuffer.allocate(100);
        CharBuffer charBuffer2 = CharBuffer.allocate(100);
        /*while (fillBuffer(charBuffer)) {
            charBuffer.flip();
            drainBuffer(charBuffer);
            charBuffer.clear();//将缓冲区重置为空状态,它并不改变缓冲区的任何元素,只是把上界limit置为容量的值,位置置为0
        }*/
        fillBuffer(charBuffer1,"abaaabc");
        fillBuffer(charBuffer2,"abaaab");
        charBuffer1.flip();
        charBuffer2.flip();
        drainBuffer(charBuffer1,0);
        drainBuffer(charBuffer2,0);
//        System.out.println(Character.compare('a','b'));
        System.out.println(charBuffer1.compareTo(charBuffer2));//若是返回负值：则charBuffer1小于charBuffer2，0：则相等，正数：则charBuffer1大于charBuffer2

        CharBuffer buffer = CharBuffer.allocate(8);
        buffer.position(3).limit(6).mark().position(5);
        CharBuffer duplicate = buffer.duplicate();//复制一个缓冲区，共享数据元素
        CharBuffer readOnlyBuffer = buffer.asReadOnlyBuffer();//与duplicate相似，只是不可以写
        buffer.clear();

        char[] myChar = new char[10];
        CharBuffer buffer1 = CharBuffer.wrap(myChar);
        buffer1.position(3).limit(6);
        CharBuffer slice = buffer1.slice();//创建分割缓冲区，映射数组位置3-5
    }
}
