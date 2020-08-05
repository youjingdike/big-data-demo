package com.xq.apitest;

import com.xq.apitest.pojo.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;

import java.util.ArrayList;
import java.util.List;

public class TransformTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 从文件读取数据
        DataStreamSource<String> inputStream = env.readTextFile("D:\\code\\FlinkTutorial_1.10\\src\\main\\resources\\sensor.txt");

        // 1. 基本转换操作：map成样例类类型
        SingleOutputStreamOperator<SensorReading> dataStream = inputStream.map((MapFunction<String, SensorReading>) value -> {
            String[] split = value.split(",");
            return new SensorReading(split[0].trim(), Long.parseLong(split[1].trim()), Double.parseDouble(split[2].trim()));
        });
        // 2. 聚合操作，首先按照id做分组，然后取当前id的最小温度
        SingleOutputStreamOperator<SensorReading> minby = dataStream.keyBy((KeySelector<SensorReading, String>) value -> value.getId())
                .minBy("temperature");

        // 3. 复杂聚合操作，reduce，得到当前id最小的温度值，以及最新的时间戳+1
        //如果是某个key的第一条数据，不执行该方法
        SingleOutputStreamOperator<SensorReading> reduce = dataStream.keyBy("id")
                .reduce((ReduceFunction<SensorReading>) (cur, newData) -> {
//                    System.out.println("cur:"+cur);
//                    System.out.println("new:"+newData);
                    return new SensorReading(cur.getId(), newData.getTimestamp() + 1, Math.min(cur.getTemperature(), newData.getTemperature()));
                });

        // 4. 分流操作，split/select，以30度为界划分高低温流
        SplitStream<SensorReading> splitStream = dataStream.split(new OutputSelector<SensorReading>() {
            @Override
            public Iterable<String> select(SensorReading value) {
                List<String> output = new ArrayList<String>();
                if (value.getTemperature() > 30) {
                    output.add("high");
                } else {
                    output.add("low");
                }
                return output;
            }
        });
        DataStream<SensorReading> highStream = splitStream.select("high");
        DataStream<SensorReading> lowStream = splitStream.select("low");
        DataStream<SensorReading> allStream = splitStream.select("high", "low");

        // 5. 合流操作，connect/comap
        SingleOutputStreamOperator<Tuple2<String, Double>> highWarningStream = highStream.map(new MapFunction<SensorReading, Tuple2<String, Double>>() {
            @Override
            public Tuple2<String, Double> map(SensorReading value) throws Exception {
                return new Tuple2<>(value.getId(), value.getTemperature());
            }
        });

        ConnectedStreams<Tuple2<String, Double>, SensorReading> connectedStream = highWarningStream.connect(lowStream);
        SingleOutputStreamOperator<Tuple3<String, Double, String>> coMapStream = connectedStream.map(new CoMapFunction<Tuple2<String, Double>, SensorReading, Tuple3<String, Double, String>>() {
            @Override
            public Tuple3<String, Double, String> map1(Tuple2<String, Double> value) throws Exception {
                return new Tuple3<>(value.f0, value.f1, "warning");
            }

            @Override
            public Tuple3<String, Double, String> map2(SensorReading value) throws Exception {
                return new Tuple3<>(value.getId(), value.getTemperature(), "normal");
            }
        });
        /*Connect 与 Union 区别：
        * 1． Union 之前两个流的类型必须是一样， Connect 可以不一样，在之后的 coMap中再去调整成为一样的。
        * 2. Connect 只能操作两个流， Union 可以操作多个。
        */
        DataStream<SensorReading> unionStream = highStream.union(lowStream);
//        minby.print("minby");
//        reduce.print("reduce");
        /*highStream.print("high");
        lowStream.print("low");
        allStream.print("all");*/
//        coMapStream.print("coMapStream");
//        unionStream.print("union");


        env.execute();


    }
}
