package com.xq.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexAppend {
    public static void main(String[] args) {
        String input = "Thanks,aaaa bbbb cccc thanks very much";
//        input = "TttttThanks,aaaa bbbb cccc thanks very much";
//        input = "Tttttthanks,aaaa bbbb cccc thanks very much";
        String regex = "([Tt])*hanks";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        StringBuffer sb = new StringBuffer();
        //循环直到遇到匹配
        while (matcher.find()) {
            if (matcher.group(1).equals("T")) {//只取最后匹配到的分组
                matcher.appendReplacement(sb, "Thank you");
            } else {
                matcher.appendReplacement(sb, "thank you");
            }
        }
        //完成到stringbuffer的传递
        matcher.appendTail(sb);
        System.out.println(sb.toString());

        //让我们再试试在替换中使用$n转义
        sb.setLength(0);
        matcher.reset();
        String replacement = "$1hank you";
        while (matcher.find()) {
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        System.out.println(sb.toString());

        //最简单的方法
        System.out.println(matcher.replaceAll(replacement));
        //使用字符串的方法
        System.out.println(input.replaceAll(regex,replacement));
    }
}
