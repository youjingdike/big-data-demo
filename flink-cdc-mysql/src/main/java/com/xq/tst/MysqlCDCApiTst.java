package com.xq.tst;

import com.ververica.cdc.connectors.mysql.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.runtime.state.storage.JobManagerCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.RowData;

public class MysqlCDCApiTst {
    public static void main(String[] args) throws Exception {
        SourceFunction<String> sourceFunction = MySqlSource.<String>builder()
                .hostname("localhost")
                .port(3306)
                .databaseList("test") // monitor all tables under inventory database
                .username("cdc")
                .password("cdc")
//                .tableList("test.cdc")
                .tableList("test.cdc","test.tst1") //要加上数据库名称
                .deserializer(new StringDebeziumDeserializationSchema()) // converts SourceRecord to String
                .startupOptions(StartupOptions.initial())
                .build();
        System.out.println("dddddd");
        Configuration conf = new Configuration();
        conf.setBoolean(ConfigConstants.LOCAL_START_WEBSERVER,true);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
        env.setRestartStrategy(RestartStrategies.noRestart());
        env.enableCheckpointing(3000, CheckpointingMode.EXACTLY_ONCE); // checkpoint every 3000 milliseconds
        env.setStateBackend(new HashMapStateBackend());
        env.getCheckpointConfig().setCheckpointStorage(new JobManagerCheckpointStorage());

//        env.addSource(sourceFunction)
//                .setParallelism(8) //source的并行度只能为1
//                .print("@@@@:").setParallelism(1); // use parallelism 1 for sink to keep message ordering
        env.addSource(sourceFunction).addSink(new SinkFunction<String>() {
            int i = 0;

            @Override
            public void invoke(String value, Context context) throws Exception {
                i++;
                System.out.println(value);
                System.out.println(i);
//                System.out.println(value.getField("op"));
            }
        }).setParallelism(1);

        env.execute();
    }
}
