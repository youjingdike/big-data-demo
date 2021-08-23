package com.xq.tst;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.runtime.state.storage.JobManagerCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import static org.apache.flink.table.api.Expressions.$;

public class PgCDCSqlTst {
    public static void main(String[] args) throws Exception {
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Configuration conf = new Configuration();
        conf.setBoolean(ConfigConstants.LOCAL_START_WEBSERVER,true);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);

        env.enableCheckpointing(3000, CheckpointingMode.EXACTLY_ONCE); // checkpoint every 3000 milliseconds
        env.setStateBackend(new HashMapStateBackend());
        env.getCheckpointConfig().setCheckpointStorage(new JobManagerCheckpointStorage());
        env.setParallelism(8);
        EnvironmentSettings blinkStreamSetting = EnvironmentSettings.newInstance()
                .useBlinkPlanner().inStreamingMode().build();
        StreamTableEnvironment tableEvn = StreamTableEnvironment.create(env, blinkStreamSetting);
        String sql = "CREATE TABLE cdc (\n" +
                "     id INT,\n" +
                "     name STRING,\n" +
                "     age INT\n" +
                "     ) WITH (\n" +
                "     'connector' = 'postgres-cdc',\n" +
                "     'hostname' = 'localhost',\n" +
                "     'port' = '5432',\n" +
                "     'username' = 'postgres',\n" +
                "     'password' = 'xq198522',\n" +
                "     'database-name' = 'test',\n" +
                "     'schema-name' = 'public',\n" +
                "     'table-name' = 'cdc'," +
                "     'slot.name' = 'tst'," +
                "     'debezium.plugin.name' = 'pgoutput')";
        tableEvn.executeSql(sql);
        Table table = tableEvn.sqlQuery("select * from cdc");
//        table.printSchema();
//        DataStream<Tuple2<Boolean, Tuple3<Integer, String, Integer>>> tuple2DataStream = tableEvn.toRetractStream(table, TypeInformation.of(new TypeHint<Tuple3<Integer, String, Integer>>() {
//        }));
        DataStream<Tuple2<Boolean, Row>> tuple2DataStream = tableEvn.toRetractStream(table, Row.class);
//        tuple2DataStream.map(new MapFunction<Tuple2<Boolean, Row>, Tuple2<Boolean, Row>>() {
//            @Override
//            public Tuple2<Boolean, Row> map(Tuple2<Boolean, Row> value) throws Exception {
//                System.out.println("op_flag:"+value.f1.getKind());
//                return value;
//            }
//        }).print("row:").setParallelism(1);
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
        SingleOutputStreamOperator<Row> resStream = filter.map((MapFunction<Tuple2<Boolean, Row>, Row>) value -> {
            Row row = value.f1;
            int arity = row.getArity();
            Object[] objects = new Object[arity + 1];
            for (int i = 0; i < arity; i++) {
                objects[i] = row.getField(i);
            }
            objects[arity]=row.getKind().toString();
            return Row.of(objects);
//            return row;
        }).returns(new RowTypeInfo(TypeInformation.of(Integer.class),TypeInformation.of(String.class),TypeInformation.of(Integer.class),TypeInformation.of(String.class)));

//        Schema schema = Schema.newBuilder().column("name", DataTypes.STRING()).column("age", DataTypes.INT()).build();
//        Table table2 = tableEvn.fromDataStream(resStream).as("name","age");

//        tableEvn.createTemporaryView("dd",resStream,$("id"),$("name"), $("age"),$("cdc_op"));
//        Table table1 = tableEvn.sqlQuery("select * from dd");
////        table1.printSchema();
//        tableEvn.toAppendStream(table1,Row.class).print("tableinfo:").setParallelism(1);

//        map.print("str:").setParallelism(1);
        resStream.print("row:").setParallelism(1);
        env.execute();
    }
}
