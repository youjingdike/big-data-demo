package com.xq.tst;

import com.ververica.cdc.connectors.mysql.debezium.EmbeddedFlinkDatabaseHistory;
import com.ververica.cdc.connectors.mysql.source.MySqlParallelSource;
import com.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.runtime.state.storage.JobManagerCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.types.Row;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.ververica.cdc.connectors.mysql.source.MySqlSourceOptions.DATABASE_SERVER_NAME;
import static org.apache.flink.util.Preconditions.checkNotNull;

public class MysqlCDCParaApiTst {
    public static void main(String[] args) throws Exception {

        MySqlParallelSource<String> parallelSource =
                new MySqlParallelSource<>(new StringDebeziumDeserializationSchema(), getParallelSourceConf());

        Configuration conf = new Configuration();
        conf.setBoolean(ConfigConstants.LOCAL_START_WEBSERVER,true);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);

//        env.enableCheckpointing(3000, CheckpointingMode.EXACTLY_ONCE); // checkpoint every 3000 milliseconds
//        env.setStateBackend(new HashMapStateBackend());
//        env.getCheckpointConfig().setCheckpointStorage(new JobManagerCheckpointStorage());
        System.out.println("@@@@@@@@@@@@@@@");

        env.getConfig().setAutoWatermarkInterval(2000);
        WatermarkStrategy.<Row>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                        .withTimestampAssigner((event,timestamp)-> (long) event.getField("timestamp"))
                        .withIdleness(Duration.ofMinutes(1));
        env.fromSource(parallelSource, WatermarkStrategy.noWatermarks(),"parallelSource")
//                .setParallelism(2) //source的并行度只能为1
                .print("@@@@:").setParallelism(1); // use parallelism 1 for sink to keep message ordering

        env.execute();
    }

    private static Configuration getParallelSourceConf() {
        Map<String, String> properties = new HashMap<>();

        properties.put("database.history", EmbeddedFlinkDatabaseHistory.class.getCanonicalName());
        properties.put("database.hostname", checkNotNull("localhost"));
        properties.put("database.user", checkNotNull("cdc"));
        properties.put("database.password", checkNotNull("cdc"));
        properties.put("database.port", String.valueOf(3306));
        properties.put("database.history.skip.unparseable.ddl", String.valueOf(true));
        properties.put("database.server.name", DATABASE_SERVER_NAME);

        /**
         * The server id is required, it will be replaced to 'database.server.id' when build {@link
         * MySqlSplitReader}
         */
        /*if (serverId != null) {
            properties.put(SERVER_ID.key(), serverId);
        }*/
//        properties.put(SCAN_INCREMENTAL_SNAPSHOT_CHUNK_SIZE.key(), String.valueOf(1111));
//        properties.put(SCAN_SNAPSHOT_FETCH_SIZE.key(), String.valueOf(122));
//        properties.put("connect.timeout.ms", String.valueOf(connectTimeout.toMillis()));

        properties.put("database.whitelist", "test");
//        properties.put("table.whitelist", "test.cdc");
//        properties.put("table.whitelist", "test.tst_no_p");
        /*if (serverTimeZone != null) {
            properties.put("database.serverTimezone", serverTimeZone.toString());
        }*/

        // set mode
        properties.put("scan.startup.mode", "initial");
//        properties.put("scan.startup.mode", "latest-offset");


        return Configuration.fromMap(properties);
    }
}
