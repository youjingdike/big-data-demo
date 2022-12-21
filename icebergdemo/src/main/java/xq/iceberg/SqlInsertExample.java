package xq.iceberg;

import com.xq.sources.RandomSourceRow;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.runtime.state.storage.FileSystemCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

public class SqlInsertExample {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.setStateBackend(new HashMapStateBackend());
        CheckpointConfig checkpointConfig = env.getCheckpointConfig();
        checkpointConfig.setCheckpointStorage(new FileSystemCheckpointStorage("file:///Users/xingqian/checkpoint-dir"));
        checkpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        checkpointConfig.setCheckpointInterval(1*1000L);
        checkpointConfig.setCheckpointTimeout(20*1000L);
        //设置同时可能正在进行的检查点尝试的最大次数。如果该值为n，则在当前有n个检查点尝试时不会触发检查点。
        // 对于要触发的下一个检查点，必须前面的一次检查点尝试需要完成或过期。
        checkpointConfig.setMaxConcurrentCheckpoints(1);
        checkpointConfig.setMinPauseBetweenCheckpoints(500L);

        DataStreamSource<Row> dataStream = (DataStreamSource<Row>) env.addSource(new RandomSourceRow(true)).returns(
                Types.ROW_NAMED(new String[] {"uuid", "name","age","ts","partition"},
                        Types.STRING,Types.STRING,Types.INT,Types.LOCAL_DATE_TIME, Types.STRING));

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // Create a HiveCatalog
        String name            = "myhive";
        String defaultDatabase = "mydatabase";
        String hiveConfDir     = "/etc/hive/2.3.7.0-1/0/";
//        Catalog catalog = new HiveCatalog(name, defaultDatabase, hiveConfDir);

        // Register the catalog
//        tableEnv.registerCatalog("myhive", catalog);
//        tableEnv.useCatalog("myhive");

        env.execute("table test");
    }
}
