package com.xq.charseq;

import java.nio.CharBuffer;

public class charSeq {
    public static void main(String[] args) {
        StringBuffer stringBuffer = new StringBuffer("Hello World");
        CharBuffer charBuffer = CharBuffer.allocate(20);
        CharSequence charSequence = "Hello World";

        //直接来源于String
        printCharSeq(charSequence);

        //来源于StringBuffer
        charSequence = stringBuffer;
        printCharSeq(charSequence);

        //更改StringBuffer
        stringBuffer.setLength(0);
        stringBuffer.append("Goodbye cruel world");
        //相同、“不变的”CharSequence产生了不同的结果
        printCharSeq(charSequence);

        //从CharBuffer中导出CharSequence
        charSequence = charBuffer;
        charBuffer.put("XXXXXXXXXXXXXXXXXXXX");
        charBuffer.clear();

        charBuffer.put("Hello World");
        charBuffer.flip();
        printCharSeq(charSequence);

        charBuffer.mark();
        charBuffer.put("Seeya");
        charBuffer.reset();
        printCharSeq(charSequence);

        charBuffer.clear();
        printCharSeq(charSequence);

    }

    private static void printCharSeq(CharSequence cs) {
        System.out.println("length="+cs.length()+",content='"+cs.toString()+"'");
    }
}
