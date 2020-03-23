package com.xq.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 匹配模式的替换
 */
public class RegexReplace {
    public static void main(String[] args) {
        String regex = "([bB]yte)";
        String input = "Bytes is bytes";
        String replacement = "$1ite";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher("");
        matcher.reset(input);
        System.out.println(matcher.replaceAll(replacement));
        System.out.println(matcher.replaceFirst(replacement));
    }
}
