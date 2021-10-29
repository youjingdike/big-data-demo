package com.xq.apitest.sinktest;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.fs.StringWriter;
import org.apache.flink.streaming.connectors.fs.bucketing.Bucketer;
import org.apache.flink.streaming.connectors.fs.bucketing.BucketingSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

import java.util.Properties;

public class BucketingSinkTest {
    public static void main(String[] args) throws Exception {
        System.setProperty("HADOOP_USER_NAME", "root");
// set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.enableCheckpointing(5000);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        Properties properties = new Properties();
//目标环境的IP地址和端口号
        properties.setProperty("bootstrap.servers", "192.168.0.1:9092");//kafka
//kafka版本0.8需要；
//        properties.setProperty("zookeeper.connect", "192.168.0.1:2181");//zookeepe
        properties.setProperty("group.id", "test-consumer-group"); //group.id
//第一种方式：
//这里很重要，填写hdfs-site.xml和core-site.xml的路径，可以把目标环境上的hadoop的这两个配置拉到本地来，这个是我放在了项目的resources目录下。
        //       properties.setProperty("fs.hdfs.hadoopconf", "E:\\Ali-Code\\cn-smart\\cn-components\\cn-flink\\src\\main\\resources");
//第二种方式：
        properties.setProperty("fs.default-scheme", "hdfs://ip:8020");

//根据不同的版本new不同的消费对象；
//        FlinkKafkaConsumer09<String> flinkKafkaConsumer09 = new FlinkKafkaConsumer09<String>("test0", new SimpleStringSchema(),properties);
        FlinkKafkaConsumer010<String> flinkKafkaConsumer010 = new FlinkKafkaConsumer010<String>("test1", new SimpleStringSchema(), properties);
//        flinkKafkaConsumer010.assignTimestampsAndWatermarks(new CustomWatermarkEmitter());
        DataStream<String> keyedStream = env.addSource(flinkKafkaConsumer010);
        keyedStream.print();
        // execute program

        System.out.println("*********** hdfs ***********************");
        BucketingSink<String> bucketingSink = new BucketingSink<>("/var"); //hdfs上的路径
        BucketingSink<String> bucketingSink1 = bucketingSink.setBucketer((Bucketer<String>) (clock, basePath, value) -> {
            return basePath;
        });
        bucketingSink.setWriter(new StringWriter<>())
                .setBatchSize(1024 * 1024)
                .setBatchRolloverInterval(2000);

        keyedStream.addSink(bucketingSink);

        env.execute("test");
    }
}

/*
在远程目标环境上hdfs的/var下面生成很多小目录，这些小目录是kafka中的数据；

        问题：
        1. 这种方式生成的hdfs文件不能够被spark sql去读取；

        解决： 将数据写成parquet格式到hdfs上可解决这个问题；见另一篇博客

        https://blog.csdn.net/u012798083/article/details/85852830

        2. 如果出现大量inprocess的文件，怎么办？

        解决： 将数据量加大一点；

        3. 如何增加窗口处理？

        解决：见另一篇博客：https://blog.csdn.net/u012798083/article/details/85852830*/
