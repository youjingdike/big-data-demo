package com.xq.hudi;

import com.xq.sources.RandomSourceRowData;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.runtime.state.storage.FileSystemCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.data.RowData;
import org.apache.hudi.common.model.HoodieTableType;
import org.apache.hudi.configuration.FlinkOptions;
import org.apache.hudi.util.HoodiePipeline;

import java.util.HashMap;
import java.util.Map;

public class InsertExample {
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

        DataStreamSource<RowData> dataStream = env.addSource(new RandomSourceRowData(true));

        /*URL resource = Example.class.getResource("/sensor.txt");
        DataStreamSource<String> inputStream = env.readTextFile(resource.getPath());*/
        String targetTable = "t2";
        String basePath = "file:///tmp/t2";
        Map<String, String> options = new HashMap<>();
        options.put(FlinkOptions.PATH.key(), basePath);
        options.put(FlinkOptions.TABLE_TYPE.key(), HoodieTableType.COPY_ON_WRITE.name());
        options.put(FlinkOptions.PRECOMBINE_FIELD.key(), "ts");

        HoodiePipeline.Builder builder = HoodiePipeline.builder(targetTable)
                .column("uuid VARCHAR(20)")
                .column("name VARCHAR(10)")
                .column("age INT")
                .column("ts TIMESTAMP(3)")
                .column("`partition` VARCHAR(20)")
                .pk("uuid")
                .partition("partition")
                .options(options);

        builder.sink(dataStream, false); // The second parameter indicating whether the input data stream is bounded
        env.execute("Hudi_Sink");
    }
}
