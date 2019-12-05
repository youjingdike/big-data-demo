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
        charBuffer.flip();
        int remaining = charBuffer.remaining();
        int num = remaining/2;
        for (int i=0;i<num-1;i++) {
            System.out.println(charBuffer.get());
        }
        charBuffer.compact();
        System.out.println("!!!!!!!!");
        charBuffer.flip();
        while (charBuffer.hasRemaining()) {
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
            charBuffer.clear();
        }*/
        fillBuffer(charBuffer);
        compactBuffer(charBuffer);
    }
}
