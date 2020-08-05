package com.xq.apitest.sinktest;

import com.xq.apitest.pojo.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class JdbcSinkTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 从文件读取数据
        DataStreamSource<String> inputStream = env.readTextFile("D:\\code\\FlinkTutorial_1.10\\src\\main\\resources\\sensor.txt");
        // 1. 基本转换操作：map成样例类类型
        SingleOutputStreamOperator<SensorReading> dataStream = inputStream.map((MapFunction<String, SensorReading>) value -> {
            String[] split = value.split(",");
            return new SensorReading(split[0].trim(), Long.parseLong(split[1].trim()), Double.parseDouble(split[2].trim()));
        });

        dataStream.addSink(new MysqlSink());

        env.execute("test mysql sink job");
    }
}

class MysqlSink extends RichSinkFunction<SensorReading> {
    private static final long serialVersionUID = 4241819183202259833L;
    private Connection conn = null;
    private PreparedStatement ins = null;
    private PreparedStatement up = null;
    @Override
    public void open(Configuration parameters) throws Exception {
        conn = DriverManager.getConnection("jdbc:mysql://node106:3306/test", "root", "Xingqian@1234");
        ins = conn.prepareStatement("insert into sensor_temp (id, temperature) values (?, ?)");
        up = conn.prepareStatement("update sensor_temp set temperature = ? where id = ?");
    }

    @Override
    public void invoke(SensorReading value, Context context) throws Exception {
        up.setDouble(1,value.getTemperature());
        up.setString(2,value.getId());
        up.execute();
        if (up.getUpdateCount()==0) {
            ins.setString(1,value.getId());
            ins.setDouble(2,value.getTemperature());
            ins.execute();
        }
    }


    @Override
    public void close() throws Exception {
        ins.close();
        up.close();
        conn.close();
    }
}