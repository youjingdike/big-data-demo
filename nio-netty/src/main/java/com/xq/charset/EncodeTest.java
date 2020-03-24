package com.xq.charset;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class EncodeTest {

    public static void main(String[] args) {
        String input = "\u00bfMa\u00f1ana?";
        String[] charsetNames = {
                "US-ASCII",
                "ISO-8859-1",
                "UTF-8",
                "UTF-16BE",
                "UTF-16LE",
                "UTF-16"
        };
        for (int i = 0; i < charsetNames.length; i++) {
            doEncode(Charset.forName(charsetNames[i]),input);
        }
    }

    private static void doEncode(Charset cs, String input) {
        ByteBuffer bb = cs.encode(input);
        System.out.println("Charset: "+cs.name());
        System.out.println("Input:" + input);
        System.out.println("Encode: ");
        for (int i = 0; bb.hasRemaining(); i++) {
            byte b = bb.get();
            int ival = ((int) b) & 0xff;
            char cval = (char) ival;

            if (i<10) {
                System.out.print(" ");
            }
            System.out.print(" "+i+": ");
            if (ival < 16) {
                System.out.print("0");
            }

            System.out.print(Integer.toHexString(ival));
            if (Character.isWhitespace(cval) || Character.isISOControl(cval)) {
                System.out.println("");
            } else {
                System.out.println("("+cval+")");
            }
        }
        System.out.println("");
    }
}
