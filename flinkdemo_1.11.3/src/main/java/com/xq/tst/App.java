package com.xq.tst;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;
import org.apache.flink.streaming.connectors.kafka.internals.KeyedSerializationSchemaWrapper;

import java.util.Properties;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception {

//                StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Configuration conf = new Configuration();
        conf.setBoolean(ConfigConstants.LOCAL_START_WEBSERVER,true);
        conf.setString(RestOptions.BIND_PORT, "8082-8089");
        conf.setInteger("state.checkpoints.num-retained",10);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
        env.setParallelism(1);
        env.enableCheckpointing(5000, CheckpointingMode.EXACTLY_ONCE);
        env.setStateBackend(new FsStateBackend("file:/Users/xingqian/flink_cp"));
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        env.setRestartStrategy(RestartStrategies.noRestart());


        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "node1.hadoop.com:9092");
        properties.setProperty("group.id", "consumer-group7");
        properties.setProperty("client.id", "11111");
//        properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        properties.setProperty("auto.offset.reset", "latest");
        properties.setProperty("auto.offset.reset", "earliest");
        SingleOutputStreamOperator<String> inputStream = env.addSource(new FlinkKafkaConsumer011<String>("xqtest", new SimpleStringSchema(), properties)).startNewChain();

        SingleOutputStreamOperator<String> stringSingleOutputStreamOperator = inputStream.map((MapFunction<String, String>) s -> {
            System.out.println("value:" + s);
            return s;
        }).startNewChain();

        Properties produceProp = new Properties();
        produceProp.setProperty("bootstrap.servers", "node1.hadoop.com:9092");
        produceProp.setProperty("client.id", "22222");
//        produceProp.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        produceProp.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        produceProp.setProperty("transaction.timeout.ms","10000");
        //transaction.max.timeout.ms
        FlinkKafkaProducer011<String> sinkFunction = new FlinkKafkaProducer011<>("test-topic", new KeyedSerializationSchemaWrapper<>(new SimpleStringSchema()), produceProp, FlinkKafkaProducer011.Semantic.EXACTLY_ONCE);
        stringSingleOutputStreamOperator.addSink(sinkFunction);
//        inputStream.print();
        env.execute("test kafka source and sink job");
    }
}
