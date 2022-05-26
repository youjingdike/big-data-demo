package com.xq.tst;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void tst() throws InterruptedException {
        Config config = new Config();
        config.setTransportMode(TransportMode.NIO);
        config.useSingleServer()
                .setPassword("123456")
                //可以用"rediss://"来启用SSL连接
                .setAddress("redis://192.168.0.100:6379");
        RedissonClient redisson = Redisson.create(config);
        CountDownLatch countDownLatch = new CountDownLatch(2);

        RBucket<String> k1 = redisson.getBucket("k1",new StringCodec("utf-8"));
        k1.getAsync().whenCompleteAsync((s, throwable) -> {
            System.out.println(s);
            countDownLatch.countDown();
        });

        RBucket<String> k2 = redisson.getBucket("k2",new StringCodec("utf-8"));
        k2.getAsync().whenCompleteAsync((s, throwable) -> {
            System.out.println(s);
            countDownLatch.countDown();
        });
        System.out.println("wait");
        countDownLatch.await();
        System.out.println("end");
    }
}
