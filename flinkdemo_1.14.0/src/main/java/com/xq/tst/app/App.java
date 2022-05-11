package com.xq.tst.app;

import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception {

        Configuration conf = new Configuration();
        conf.setBoolean(ConfigConstants.LOCAL_START_WEBSERVER,true);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);

        env.enableCheckpointing(3000, CheckpointingMode.EXACTLY_ONCE); // checkpoint every 3000 milliseconds


        env.addSource(new SourceFunction<String>() {
                    @Override
                    public void run(SourceContext<String> sourceContext) throws Exception {

                    }

                    @Override
                    public void cancel() {

                    }
                })
//                .setParallelism(8) //source的并行度只能为1
                .print("@@@@:").setParallelism(1); // use parallelism 1 for sink to keep message ordering

        env.execute();
    }
}
