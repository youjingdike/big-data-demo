package com.xq.wc;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class WordCount {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        DataSource<String> dataSource = env.readTextFile("D:\\code\\bigdatademo\\flinkdemo\\src\\main\\resources\\sensor.txt");

        AggregateOperator<Tuple2<String, Integer>> resultData = dataSource.flatMap(new FlatMapFunction<String, String>() {
            private static final long serialVersionUID = -7754632955840933808L;

            public void flatMap(String s, Collector<String> collector) throws Exception {
                String[] split = s.split(",");
                for (String ss : split) {
                    collector.collect(ss);
                }
            }
        }).map(new MapFunction<String, Tuple2<String, Integer>>() {
            private static final long serialVersionUID = 937981038057746648L;

            public Tuple2<String, Integer> map(String s) throws Exception {
                return new Tuple2<String, Integer>(s, 1);
            }
        }).groupBy(0)
                .sum(1);

        resultData.print();

    }

}
