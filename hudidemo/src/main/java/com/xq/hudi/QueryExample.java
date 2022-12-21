package com.xq.hudi;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.data.RowData;
import org.apache.hudi.common.model.HoodieTableType;
import org.apache.hudi.configuration.FlinkOptions;
import org.apache.hudi.util.HoodiePipeline;

import java.util.HashMap;
import java.util.Map;

public class QueryExample {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        String targetTable = "t3";
//        String basePath = "file:///tmp/t3";
        String basePath = "hdfs:///user/flink/t3";

        Map<String, String> options = new HashMap<>();
        options.put(FlinkOptions.PATH.key(), basePath);
        options.put(FlinkOptions.TABLE_TYPE.key(), HoodieTableType.COPY_ON_WRITE.name());
        options.put(FlinkOptions.READ_AS_STREAMING.key(), "true"); // this option enable the streaming read
        options.put(FlinkOptions.READ_START_COMMIT.key(), "1200316134557"); // specifies the start commit instant time

        HoodiePipeline.Builder builder = HoodiePipeline.builder(targetTable)
                .column("uuid VARCHAR(20)")
                .column("name VARCHAR(10)")
                .column("age INT")
                .column("ts TIMESTAMP(3)")
                .column("`partition` VARCHAR(20)")
                .pk("uuid")
                .partition("partition")
                .options(options);

        DataStream<RowData> rowDataDataStream = builder.source(env);
        rowDataDataStream.print();
        env.execute("Hudi_Source");
    }
}
