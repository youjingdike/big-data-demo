package com.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Gzip {

    //压缩
    public static String compress(String str) throws IOException {

        ByteArrayOutputStream out=new ByteArrayOutputStream();

        GZIPOutputStream gout=new GZIPOutputStream(out);

        gout.write(str.getBytes());

        gout.close();

        return out.toString("ISO-8859-1");

    }

    //解压缩
    public static String uncompress(String str) throws IOException{

        ByteArrayOutputStream out=new ByteArrayOutputStream();

        ByteArrayInputStream in=new ByteArrayInputStream(str.getBytes("ISO-8859-1"));

        GZIPInputStream gin=new GZIPInputStream(in);

        byte[] buffer=new byte[256];

        int n;

        while((n=gin.read(buffer))>=0){

            out.write(buffer,0,n);

        }

        return out.toString();

    }

    //测试
    public static void main(String[] args) throws IOException {

        //测试字符串

        String str="%5B%7B%22lastUpdateTime%22%3A%222011-10-28+9%3A39%3A41%22%2C%22smsList%22%3A%5B%7B%22liveState%22%3A%221";

        System.out.println("原长度："+str.length());

        System.out.println("压缩后："+Gzip.compress(str).length());

        System.out.println("解压缩："+Gzip.uncompress(Gzip.compress(str)));

    }


}
