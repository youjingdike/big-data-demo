package com.xq.apitest;

import com.xq.apitest.pojo.SensorReading;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class SourceTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<SensorReading> s1 = env.fromCollection(Arrays.asList(new SensorReading[]{
                new SensorReading("sensor_1", 1547718199L, 35.8D),
                new SensorReading("sensor_6", 1547718201L, 15.4D),
                new SensorReading("sensor_7", 1547718202L, 6.7D),
                new SensorReading("sensor_10", 1547718205L, 38.1D)
        }));

        DataStreamSource<String> s2 = env.fromElements("3a","5dd");

        DataStreamSource<SensorReading> s3 = env.addSource(new RichSourceFunction<SensorReading>() {
            private static final long serialVersionUID = -5851350892235751957L;
            private boolean isCancel = false;

            public void run(SourceContext<SensorReading> sourceContext) throws Exception {
                Random random = new Random();

                Map<Integer, Double> map = new HashMap(10);
                for (int i = 0; i < 10; i++) {
                    map.put(i, 60 + random.nextGaussian() * 20);
                }

                while (!isCancel) {
                    Iterator<Map.Entry<Integer, Double>> iterator = map.entrySet().iterator();
                    for (; iterator.hasNext(); ) {
                        Map.Entry<Integer, Double> next = iterator.next();
                        map.put(next.getKey(), next.getValue() + random.nextGaussian());
                    }
                    final long currentTimeMillis = System.currentTimeMillis();
                    map.forEach((k, v) -> {
                        System.out.println(k+":"+v);
                        sourceContext.collect(new SensorReading(k.toString(), currentTimeMillis, v));
                    });
                    // 间隔200ms
                    Thread.sleep(200);
                }
            }

            public void cancel() {
                isCancel = true;
            }
        });

//        s1.print("s1");
//        s2.print("s2");
        s3.print("s3");

        env.execute();
    }
}