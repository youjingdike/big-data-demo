package xq.iceberg;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class QueryExample {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        String targetTable = "t3";
//        String basePath = "file:///tmp/t3";
        String basePath = "hdfs:///user/flink/t3";

        env.execute("Hudi_Source");
    }
}
