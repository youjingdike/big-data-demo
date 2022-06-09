package com.xq.tst;

import static org.junit.Assert.assertTrue;

import io.netty.util.CharsetUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.redisson.config.TransportMode;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Unit test for simple App.
 */
public class AppTest {
    RedissonClient redisson;

    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Before
    public void be() {
        //单机模式：
        Config config = new Config();
        config.setTransportMode(TransportMode.NIO);
        config.useSingleServer()
                .setPassword("123456")
                //可以用"rediss://"来启用SSL连接
                .setAddress("redis://10.91.197.148:6379");
//                .setAddress("redis://10.91.197.148:6379");
//                .setAddress("redis://192.168.0.100:6379");

        /*//主从模式：
        config.useMasterSlaveServers()
                //可以用"rediss://"来启用SSL连接
                .setMasterAddress("redis://127.0.0.1:6379")
                .setReadMode(ReadMode.MASTER_SLAVE)
                .setTimeout(3000)
                .setConnectTimeout(10000)
                .addSlaveAddress("redis://127.0.0.1:6389", "redis://127.0.0.1:6332", "redis://127.0.0.1:6419")
                .addSlaveAddress("redis://127.0.0.1:6399");

        //哨兵模式：
        config.useSentinelServers()
                .setMasterName("mymaster")
                //可以用"rediss://"来启用SSL连接
                .addSentinelAddress("127.0.0.1:26389", "127.0.0.1:26379")
                .addSentinelAddress("127.0.0.1:26319");

        //集群模式：
        config.useClusterServers()
                .setScanInterval(2000) // 集群状态扫描间隔时间，单位是毫秒
                //可以用"rediss://"来启用SSL连接
                .addNodeAddress("redis://127.0.0.1:7000", "redis://127.0.0.1:7001")
                .addNodeAddress("redis://127.0.0.1:7002");*/

        redisson = Redisson.create(config);
    }

    @Test
    public void tstString() throws InterruptedException {

//        CountDownLatch countDownLatch = new CountDownLatch(2);

        RBucket<String> k1 = redisson.getBucket("k1",new StringCodec("utf-8"));
        k1.getAsync().whenCompleteAsync((s, throwable) -> {
            System.out.println(s);
//            countDownLatch.countDown();
        });

        RBucket<String> k2 = redisson.getBucket("k2",StringCodec.INSTANCE);
        k2.getAsync().whenCompleteAsync((s, throwable) -> {
            System.out.println(s);
//            countDownLatch.countDown();
        });
        System.out.println("wait");
//        countDownLatch.await();
//        System.out.println("end");

    }

    @Test
    public void tstZsetAdd() throws InterruptedException {

        RScoredSortedSet<String> set = redisson.getScoredSortedSet("setKey", new StringCodec(CharsetUtil.UTF_8));
        set.add(10,"ddd");
        Map<String, Double> newValues = new HashMap<>();
        newValues.put("4", 40D);
        newValues.put("5", 50D);
        newValues.put("6", 60D);
        int newValuesAmount = set.addAll(newValues);

    }

    @Test
    public void tstZset() throws InterruptedException, ExecutionException {

        String key = "setKey";
        RScoredSortedSet<String> set = redisson.getScoredSortedSet(key, new StringCodec(CharsetUtil.UTF_8));
        CompletableFuture<Collection<MyPojo>> completableFuture = new CompletableFuture<>();
        MyPojo myPojo = new MyPojo();
//        CountDownLatch countDownLatch = new CountDownLatch(1);
        /*set.valueRangeAsync(10, true, 40, true).whenCompleteAsync((strings, throwable) -> {
            System.out.println("~~~~~~~~~~~~");
            strings.stream().forEach(System.out::println);
            System.out.println("~~~~~~~~~~~~");
//            countDownLatch.countDown();
        });*/
        getZset(key,completableFuture,0,myPojo,10D,40D,set);
        System.out.println("wait");
        completableFuture.get().stream().forEach(myPojo1 -> System.out.println(myPojo1.getName()));
//        countDownLatch.await();

    }

    private void getZset(String key, CompletableFuture<Collection<MyPojo>> future,int current,MyPojo myPojo,Double start,Double end,RScoredSortedSet<String> set) {
        set.valueRangeAsync(start,true,end,true).whenCompleteAsync((strings, throwable) -> {
            if (current <= 5) {
                System.out.println("current:"+current);
                getZset(key, future, current+1, myPojo, start, end, set);
            } else {
                StringBuffer sb = new StringBuffer();
                strings.stream().forEach(s -> {
                    sb.append(s).append(",");
                });
                myPojo.setKey(key);
                myPojo.setName(sb.toString());
                future.complete(Collections.singletonList(myPojo));
            }
        });
    }

    @After
    public void af () {
        if (redisson != null) {
            System.out.println("end");
            redisson.shutdown();
        }
    }
}
