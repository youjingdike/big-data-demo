package com.xq.tst;

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

public class StreamingFileSinkTest {
    public static void main(String[] args) throws Exception {
        System.setProperty("HADOOP_USER_NAME", "root");
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Configuration conf = new Configuration();
        conf.setBoolean(ConfigConstants.LOCAL_START_WEBSERVER,true);
//        conf.setBoolean("fs.output.always-create-directory",true);
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
            return String.join(",",split[0].trim(), split[1].trim(), split[2].trim());
        });

        // 直接写入文件
//        dataStream.writeAsText("D:\\code\\FlinkTutorial_1.10\\src\\main\\resources\\out");
//        dataStream.writeAsText("hdfs://node101:9000/flink/", FileSystem.WriteMode.OVERWRITE);

        /**
         * 可以看FlinkTutorial_1.10_New项目里的案例
         */
        StreamingFileSink<String> build = StreamingFileSink.forRowFormat(
                new Path("hdfs://node101:9000/flink/hdfsSink/"),
                new SimpleStringEncoder<String>("UTF-8")
        ).build();

        dataStream.addSink(build);
        env.execute("test kafka source and sink job");
    }
}
