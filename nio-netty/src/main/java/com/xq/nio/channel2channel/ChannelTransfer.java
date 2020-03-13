package com.xq.nio.channel2channel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

public class ChannelTransfer {
    public static void main(String[] args) throws Exception {
        String[] files = null;
        if (args.length == 0) {
/*            System.err.println("Usage: filename...");
            return;*/
            files = new String[]{"D:\\code\\bigdatademo\\nio-netty\\src\\main\\resources\\test.txt"};
        } else {
            files = args;
        }
        catFiles(Channels.newChannel(System.out),files);
    }


    private static void catFiles(WritableByteChannel target,String [] files) throws Exception {
        for (int i = 0; i < files.length; i++) {
            FileInputStream fis = new FileInputStream(files[i]);
            FileChannel channel = fis.getChannel();
            channel.transferTo(0,channel.size(),target);
            channel.close();
            fis.close();
        }

    }
}
