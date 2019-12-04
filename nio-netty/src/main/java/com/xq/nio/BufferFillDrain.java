package com.xq.nio;

import java.nio.CharBuffer;

/**
 * Hello world!
 *
 */
public class BufferFillDrain {
    private static int index = 0;
    private static String[] strArr = {
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
        while (fillBuffer(charBuffer)) {
            charBuffer.flip();
            drainBuffer(charBuffer);
            charBuffer.clear();
        }

    }
}
