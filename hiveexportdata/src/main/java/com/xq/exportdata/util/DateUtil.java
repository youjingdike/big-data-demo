package com.xq.exportdata.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static  Date parseStr(String str) throws ParseException {
        return sdf.parse(str);
    }

    public static void main(String[] args) throws ParseException {
        System.out.println(parseStr("2015-04-26 12:12:12"));
    }
}
