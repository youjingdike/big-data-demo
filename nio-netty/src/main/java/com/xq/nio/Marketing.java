package com.xq.nio;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Marketing {

    private static final String DEMOGRAPHIC = "blahblah.txt";

    public static void main(String[] args) throws IOException {
        int reps = 10;
        if (args.length > 0) {
            reps = Integer.parseInt(args[0]);
        }

        FileOutputStream fos = new FileOutputStream(DEMOGRAPHIC);
        GatheringByteChannel gatheringByteChannel = fos.getChannel();
        ByteBuffer[] byteBuffers = utterBS(reps);
        while (gatheringByteChannel.write(byteBuffers)>0) {
        }
        System.out.println("Mindshare paradigms synergized to " + DEMOGRAPHIC);

        fos.close();
    }

    private static String[] col1 = {
            "adfasf11",
            "adfasf12",
            "adfasf13",
            "adfasf14"
    };
    private static String[] col2 = {
            "adfasf21",
            "adfasf22",
            "adfasf23",
            "adfasf24"
    };
    private static String[] col3 = {
            "adfasf31",
            "adfasf32",
            "adfasf33",
            "adfasf44"
    };

    private static String newLine = System.getProperty("line.separator");

    private static ByteBuffer[] utterBS(int howMany) {

        List list = new LinkedList<ByteBuffer>();

        for (int i = 0; i < howMany; i++) {
            list.add(pickRandon(col1, " "));
            list.add(pickRandon(col2, " "));
            list.add(pickRandon(col3, newLine));
        }

        ByteBuffer[] byteBuffers = new ByteBuffer[(list.size())];
        list.toArray(byteBuffers);

        return byteBuffers;
    }

    private static Random random = new Random();
    private static ByteBuffer pickRandon(String[] strings,String suffix) {
        String string = strings[random.nextInt(strings.length)];
        int total = string.length() + suffix.length();
        ByteBuffer buf = ByteBuffer.allocate(total);
        buf.put(string.getBytes(Charset.forName("US-ASCII")));
        buf.put(suffix.getBytes(Charset.forName("US-ASCII")));
        buf.flip();
        return buf;
    }
}
