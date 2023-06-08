package com.xq.tst;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.Path;
import org.apache.flink.encoder.SimpleRowEncoder;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.runtime.state.storage.FileSystemCheckpointStorage;
import org.apache.flink.runtime.state.storage.JobManagerCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.xq.StreamingFileMultiSink;
import org.apache.flink.streaming.api.functions.sink.xq.bucketassigners.DateTimeBucketAssigner;
import org.apache.flink.streaming.api.functions.sink.xq.rollingpolicies.OnCheckpointRollingPolicy;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.types.Row;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception {
        Configuration conf = new Configuration();
        conf.setBoolean(ConfigConstants.LOCAL_START_WEBSERVER,true);
        conf.setString("fs.defaultFS","hdfs://localhost:9000");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);

        env.enableCheckpointing(60000, CheckpointingMode.EXACTLY_ONCE); // checkpoint every 3000 milliseconds
        env.setStateBackend(new HashMapStateBackend());
        env.getCheckpointConfig().setCheckpointStorage(new JobManagerCheckpointStorage());
        env.getCheckpointConfig().setCheckpointStorage(new FileSystemCheckpointStorage("hdfs://localhost:9000/flink/ckp"));
        env.setRestartStrategy(RestartStrategies.noRestart());

        DataStreamSource<Row> rowDataStreamSource = env.addSource(new RichSourceFunction<Row>() {
            private boolean isCancel = false;

            public void run(SourceContext<Row> sourceContext) throws Exception {
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
//                        System.out.println(k + ":" + v);
                        Map<String, Object> columnInfo = new HashMap<>();
                        //放入表名
                        columnInfo.put("tableName", "tst" + random.nextInt(4));

                        columnInfo.put("name", "xq"+ LocalDateTime.now());
                        columnInfo.put("age", random.nextInt(4));
                        sourceContext.collect(Row.of(columnInfo));
                    });
                    // 间隔200ms
                    Thread.sleep(200);
                }
            }

            public void cancel() {
                isCancel = true;
            }
        });
//        rowDataStreamSource.print();
        SingleOutputStreamOperator<Row> map = rowDataStreamSource.map((MapFunction<Row, Row>) value -> {
            System.out.println(value);
            return value;
        });
        List<StreamingFileMultiSink.BucketsBuilder> rowFormatBuilders = new ArrayList<>();
        rowFormatBuilders.add(StreamingFileMultiSink.forRowFormat(new Path("hdfs://localhost:9000/tst0"), new SimpleRowEncoder<Row>(","), "tst0")
            .withBucketAssigner(new DateTimeBucketAssigner()).withRollingPolicy(OnCheckpointRollingPolicy.build()));
        rowFormatBuilders.add(StreamingFileMultiSink.forRowFormat(new Path("hdfs://localhost:9000/tst1"), new SimpleRowEncoder<Row>("@"), "tst1")
            .withBucketAssigner(new DateTimeBucketAssigner()).withRollingPolicy(OnCheckpointRollingPolicy.build()));
        rowFormatBuilders.add(StreamingFileMultiSink.forRowFormat(new Path("hdfs://localhost:9000/tst2"), new SimpleRowEncoder<Row>(","), "tst2")
            .withBucketAssigner(new DateTimeBucketAssigner()).withRollingPolicy(OnCheckpointRollingPolicy.build()));
        rowFormatBuilders.add(StreamingFileMultiSink.forRowFormat(new Path("hdfs://localhost:9000/tst3"), new SimpleRowEncoder<Row>("\t"), "tst3")
            .withBucketAssigner(new DateTimeBucketAssigner()).withRollingPolicy(OnCheckpointRollingPolicy.build()));

        map.addSink(new StreamingFileMultiSink(rowFormatBuilders, 3000L));

        env.execute();
    }

}
