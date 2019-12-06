package com.xq.nio;

import java.nio.CharBuffer;

/**
 * Hello world!
 *
 */
public class BufferFillDrain {
    private static int index = 0;
    private static String[] strArr = {
            "abcdefg",
            "A random string value",
            "tejljej",
            "jsjfjkjljl",
            "jjjjjjj",
            "kkkkkkk",
            "iiiiiii"
    };

    private static void drainBuffer(CharBuffer charBuffer) {
        while (charBuffer.hasRemaining()) {
            System.out.println(charBuffer.get());
        }
        System.out.println("@@@@@@@@@@@@@");
    }

    private static void compactBuffer(CharBuffer charBuffer) {
        charBuffer.flip();//该发放把处在填充状态的缓存区转换成准备读出元素的释放状态
        int remaining = charBuffer.remaining();//该方法会告诉你从当前位置到上界还剩余的元素个数
        int num = remaining/2;
        for (int i=0;i<num-1;i++) {
            System.out.println(charBuffer.get());
        }
        System.out.println("!!!!!!!!");
        charBuffer.rewind();//与flip相似,但是不影响limit,只将位置置0,一般用于后退,重新读取被翻转的缓冲区的数据
        remaining = charBuffer.remaining();
        num = remaining/2;
        for (int i=0;i<num-1;i++) {
            System.out.println(charBuffer.get());
        }
        System.out.println("###########");

        charBuffer.compact();//将缓冲区进行压缩,未读出的元素向前移动,位置指定到压缩后的最后的元素的位置,上界置为容量大小,缓存重新回到填充状态
        charBuffer.flip();
        while (charBuffer.hasRemaining()) {//该方法会告诉你释放缓存区时,是否已经打到缓冲区的上界limit
            System.out.println(charBuffer.get());
        }
        System.out.println("@@@@@@@@@@@@@");
    }

    private static boolean fillBuffer(CharBuffer charBuffer) {
        if (index >= strArr.length) {
            return false;
        }

        String str = strArr[index++];
        for (int i=0; i<str.length();i++) {
            charBuffer.put(str.charAt(i));
        }
        return true;
    }

    public static void main( String[] args ) {
        CharBuffer charBuffer = CharBuffer.allocate(100);
        /*while (fillBuffer(charBuffer)) {
            charBuffer.flip();
            drainBuffer(charBuffer);
            charBuffer.clear();//将缓冲区重置为空状态,它并不改变缓冲区的任何元素,只是把上界limit置为容量的值,位置置为0
        }*/
        fillBuffer(charBuffer);
        compactBuffer(charBuffer);
    }
}
