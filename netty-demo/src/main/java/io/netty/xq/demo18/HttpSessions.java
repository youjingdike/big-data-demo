package io.netty.xq.demo18;

import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xq on 2018/9/6.
 */
public class HttpSessions {

    public static Map<String, NioSocketChannel> channelMap = new ConcurrentHashMap<>();
}
