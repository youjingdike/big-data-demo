package com.xq.tst;

import com.ververica.cdc.connectors.postgres.PostgreSQLSource;
import com.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.runtime.state.storage.JobManagerCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.Properties;

public class PgCDCApiTst {
    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.setProperty("plugin.name", "pgoutput");
        SourceFunction<String> sourceFunction = PostgreSQLSource.<String>builder()
                .hostname("localhost")
                .port(5432)
                .database("test") // monitor all tables under inventory database
                .schemaList("public")
                .username("postgres")
                .password("xq198522")
                .slotName("tst_xq")
                .tableList("public.cdc")//要加上schema名称
                .debeziumProperties(properties)
                .deserializer(new StringDebeziumDeserializationSchema()) // converts SourceRecord to String
                .build();
//        System.out.println("dddddd");
        Configuration conf = new Configuration();
        conf.setBoolean(ConfigConstants.LOCAL_START_WEBSERVER,true);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);

        env.enableCheckpointing(3000, CheckpointingMode.EXACTLY_ONCE); // checkpoint every 3000 milliseconds
        env.setStateBackend(new HashMapStateBackend());
        env.getCheckpointConfig().setCheckpointStorage(new JobManagerCheckpointStorage());

        env.setParallelism(8);
        env.addSource(sourceFunction)
                .print("@@@@:").setParallelism(1); // use parallelism 1 for sink to keep message ordering

        env.execute();
    }
}
