package com.xq.tst;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.api.java.typeutils.runtime.RowSerializer;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.runtime.state.storage.JobManagerCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.*;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.planner.typeutils.RowTypeUtils;
import org.apache.flink.table.types.DataType;
import org.apache.flink.types.Row;
import org.apache.flink.types.RowKind;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.apache.flink.table.api.Expressions.$;

public class MysqlCDCSqlTst {
    public static void main(String[] args) throws Exception {
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Configuration conf = new Configuration();
        conf.setBoolean(ConfigConstants.LOCAL_START_WEBSERVER,true);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);

        env.enableCheckpointing(3000, CheckpointingMode.EXACTLY_ONCE); // checkpoint every 3000 milliseconds
        env.setStateBackend(new HashMapStateBackend());
        env.getCheckpointConfig().setCheckpointStorage(new JobManagerCheckpointStorage());
        env.setParallelism(8);
        EnvironmentSettings blinkBatchSetting = EnvironmentSettings.newInstance()
                .useBlinkPlanner().inStreamingMode().build();
        StreamTableEnvironment tableEvn = StreamTableEnvironment.create(env, blinkBatchSetting);
        String sql = "CREATE TABLE cdc (\n" +
                "     name STRING,\n" +
                "     age INT,\n" +
                "     PRIMARY KEY(name) NOT ENFORCED\n" +
                "     ) WITH (\n" +
                "     'connector' = 'mysql-cdc',\n" +
                "     'hostname' = 'localhost',\n" +
                "     'port' = '3306',\n" +
                "     'username' = 'cdc',\n" +
                "     'password' = 'cdc',\n" +
                "     'database-name' = 'test',\n" +
                "     'table-name' = 'tst1')";
        tableEvn.executeSql(sql);
        Table table = tableEvn.sqlQuery("select * from cdc");
//        DataStream<Tuple2<Boolean, Tuple3<Integer, String, Integer>>> tuple2DataStream = tableEvn.toRetractStream(table, TypeInformation.of(new TypeHint<Tuple3<Integer, String, Integer>>() {
//        }));
        DataStream<Tuple2<Boolean, Row>> tuple2DataStream = tableEvn.toRetractStream(table, Row.class);
//        tuple2DataStream.print("row:").setParallelism(1);
        SingleOutputStreamOperator<Tuple2<Boolean, Row>> filter = tuple2DataStream.filter((FilterFunction<Tuple2<Boolean, Row>>) value -> !"UPDATE_BEFORE".equalsIgnoreCase(value.f1.getKind().toString()));
//        SingleOutputStreamOperator<String> map = filter.map((MapFunction<Tuple2<Boolean, Row>, String>) value -> {
//            Row row = value.f1;
//            String str = row.getKind().toString() + ":";
//            int arity = row.getArity();
//            for (int i = 0; i < arity; i++) {
//                str += row.getFieldAs(i) + ",";
//            }
//            str += row.getField("name");
//            return str;
//        });
        SingleOutputStreamOperator<Row> map1 = filter.map((MapFunction<Tuple2<Boolean, Row>, Row>) value -> {
            Row row = value.f1;
            int arity = row.getArity();
            Object[] objects = new Object[arity + 1];
            for (int i = 0; i < arity; i++) {
                objects[i] = row.getField(i);
            }
            objects[arity]=row.getKind().toString();
            return Row.of(objects);
//            return row;
        }).returns(new RowTypeInfo(TypeInformation.of(String.class),TypeInformation.of(Integer.class),TypeInformation.of(String.class)));
//        Schema schema = Schema.newBuilder().column("name", DataTypes.STRING()).column("age", DataTypes.INT()).build();
//        Table table2 = tableEvn.fromDataStream(map1).as("name","age");
        tableEvn.createTemporaryView("dd",map1,$("name"), $("age"),$("cdc_op"));
        Table table1 = tableEvn.sqlQuery("select * from dd");
        table1.printSchema();
        tableEvn.toAppendStream(table1,Row.class).print("tableinfo:");

//        map.print("str:").setParallelism(1);
        map1.print("row1:").setParallelism(1);
        env.execute();
    }
}
