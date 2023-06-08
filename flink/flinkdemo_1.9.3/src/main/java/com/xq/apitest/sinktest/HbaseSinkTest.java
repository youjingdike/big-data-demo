package com.xq.apitest.sinktest;

import com.xq.apitest.pojo.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

public class HbaseSinkTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 从文件读取数据
        DataStreamSource<String> inputStream = env.readTextFile("D:\\code\\bigdatademo\\flinkdemo\\src\\main\\resources\\sensor.txt");
        // 1. 基本转换操作：map成样例类类型
        SingleOutputStreamOperator<SensorReading> dataStream = inputStream.map((MapFunction<String, SensorReading>) value -> {
            String[] split = value.split(",");
            return new SensorReading(split[0].trim(), Long.parseLong(split[1].trim()), Double.parseDouble(split[2].trim()));
        });

        dataStream.addSink(new HbaseSink());

        env.execute("test hbase sink job");
    }
}

class HbaseSink extends RichSinkFunction<SensorReading> implements CheckpointedFunction {
    private static final long serialVersionUID = 4241819183202259833L;
    @Override
    public void open(Configuration parameters) throws Exception {
    }

    @Override
    public void invoke(SensorReading value, Context context) throws Exception {
    }


    @Override
    public void close() throws Exception {
    }

    @Override
    public void snapshotState(FunctionSnapshotContext context) throws Exception {
        //在这里调用数据保存
    }

    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        //不需要做处理
    }
}