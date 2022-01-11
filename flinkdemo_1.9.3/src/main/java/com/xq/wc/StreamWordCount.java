package com.xq.wc;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class StreamWordCount {
    public static void main(String[] args) throws Exception {
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Configuration conf = new Configuration();
        conf.setBoolean(ConfigConstants.LOCAL_START_WEBSERVER,true);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
        env.setParallelism(8);
        env.disableOperatorChaining();
        System.out.println(env.getConfig().getGlobalJobParameters());
        env.getConfig().getGlobalJobParameters().toMap().forEach((k,v)->{
            System.out.println(k+":"+v);
        });
        DataStreamSource<String> streamSource = env.fromElements("sdfsd,dfsdfd", "sdfsdc,dfsdf");
//        DataStreamSource<String> streamSource = env.socketTextStream("localhost", 7777);
        SingleOutputStreamOperator<Tuple2<String, Integer>> sum = streamSource.flatMap(new FlatMapFunction<String, String>() {
            private static final long serialVersionUID = 7035522967585184429L;

            public void flatMap(String s, Collector<String> collector) throws Exception {
                String[] split = s.split(",");
                for (String s1 : split) {
                    collector.collect(s1);
                }
            }
        }).map(new MapFunction<String, Tuple2<String, Integer>>() {
            private static final long serialVersionUID = -5467305118382148361L;

            public Tuple2<String, Integer> map(String s) throws Exception {
                return new Tuple2<String, Integer>(s, 1);
            }
        }).startNewChain().keyBy(0)
                .sum(1);

        sum.print("stream wc").setParallelism(1);
        env.execute("stream wc job");
    }
}
