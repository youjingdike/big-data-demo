package com.xq.apitest.sinktest;

import com.xq.apitest.pojo.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;

import java.util.Properties;

public class HdfsSinkTest {
    public static void main(String[] args) throws Exception {
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Configuration conf = new Configuration();
        conf.setBoolean(ConfigConstants.LOCAL_START_WEBSERVER,true);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
        env.setParallelism(1);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "node102:9092");
        properties.setProperty("group.id", "consumer-group");
        properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("auto.offset.reset", "latest");
// 从文件读取数据
        DataStreamSource<String> inputStream = env.readTextFile("D:\\code\\bigdatademo\\flinkdemo\\src\\main\\resources\\sensor.txt");
//        DataStreamSource<String> inputStream = env.addSource(new FlinkKafkaConsumer011<String>("sensor", new SimpleStringSchema(), properties));
        // 1. 基本转换操作：map成样例类类型
        SingleOutputStreamOperator<String> dataStream = inputStream.map((MapFunction<String, String>) value -> {
            String[] split = value.split(",");
            return new SensorReading(split[0].trim(), Long.parseLong(split[1].trim()), Double.parseDouble(split[2].trim())).toString();
        });

        // 直接写入文件
//        dataStream.writeAsText("D:\\code\\FlinkTutorial_1.10\\src\\main\\resources\\out");

        dataStream.addSink( StreamingFileSink.forRowFormat(
                new Path("/flink/hdfsSink"),
                new SimpleStringEncoder<String>("UTF-8")
        ).build());
        env.execute("test kafka source and sink job");
    }
}
