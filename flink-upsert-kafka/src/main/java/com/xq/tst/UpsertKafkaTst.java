package com.xq.tst;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.runtime.state.storage.JobManagerCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

public class UpsertKafkaTst {
    public static void main(String[] args) throws Exception {
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Configuration conf = new Configuration();
        conf.setBoolean(ConfigConstants.LOCAL_START_WEBSERVER,true);
//        conf.setInteger(ConfigConstants.JOB_MANAGER_WEB_PORT_KEY,8082);
        conf.setString(RestOptions.BIND_PORT, "8081-8089");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);

        env.enableCheckpointing(6000, CheckpointingMode.EXACTLY_ONCE); // checkpoint every 3000 milliseconds
        env.setStateBackend(new HashMapStateBackend());
        env.getCheckpointConfig().setCheckpointStorage(new JobManagerCheckpointStorage());
        env.setParallelism(4);
        EnvironmentSettings blinkBatchSetting = EnvironmentSettings.newInstance()
                .useBlinkPlanner().inStreamingMode().build();
        StreamTableEnvironment tableEvn = StreamTableEnvironment.create(env, blinkBatchSetting);
        String sql = "CREATE TABLE cdc (\n" +
                "     id INT,\n" +
                "     name STRING,\n" +
                "     age INT,\n" +
                "     PRIMARY KEY(id) NOT ENFORCED\n" +
                "     ) WITH (\n" +
                "     'connector' = 'mysql-cdc',\n" +
                "     'hostname' = 'localhost',\n" +
                "     'port' = '3306',\n" +
                "     'username' = 'cdc',\n" +
                "     'password' = 'cdc',\n" +
                "     'database-name' = 'test',\n" +
//                "     'scan.startup.mode' = 'latest-offset',\n" +
                "     'scan.startup.mode' = 'initial',\n" +
//                "     'scan.snapshot.fetch.size' = '10',\n" +
//                "     'scan.incremental.snapshot.chunk.size' = '8096',\n" +
                "     'table-name' = 'cdc')";
        System.out.println(sql);
        tableEvn.executeSql(sql);

        sql = "CREATE TABLE tst (\n" +
                "     id INT,\n" +
                "     name STRING,\n" +
                "     age INT,\n" +
                "     PRIMARY KEY(id) NOT ENFORCED\n" +
                ") WITH (\n" +
                "  'connector' = 'upsert-kafka',\n" +
                "  'topic' = 'upsert-tst',\n" +
                "  'properties.bootstrap.servers' = 'localhost:9092',\n" +
                "  'key.format' = 'json',\n" +
                "  'value.format' = 'json'\n" +
                ")";

        System.out.println(sql);
        tableEvn.executeSql(sql);
        sql = " INSERT INTO tst \n" +
                "SELECT \n" +
                "  id,\n" +
                "  name,\n" +
                "  age \n" +
                "FROM cdc ";
//        TableResult tableResult =
                tableEvn.executeSql(sql);
        System.out.println(sql);
//        tableResult.print();
        Table table = tableEvn.sqlQuery("select * from tst");
//        DataStream<Tuple2<Boolean, Tuple3<Integer, String, Integer>>> tuple2DataStream = tableEvn.toRetractStream(table, TypeInformation.of(new TypeHint<Tuple3<Integer, String, Integer>>() {
//        }));
        DataStream<Tuple2<Boolean, Row>> tuple2DataStream = tableEvn.toRetractStream(table, Row.class);
        tuple2DataStream.print("row:").setParallelism(1);
//        SingleOutputStreamOperator<Tuple2<Boolean, Row>> filter = tuple2DataStream.filter((FilterFunction<Tuple2<Boolean, Row>>) value -> !"UPDATE_BEFORE".equalsIgnoreCase(value.f1.getKind().toString()));
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

//        String[] fieldNames = new String[3];
//        fieldNames[0] = "id";
//        fieldNames[1] = "name";
//        fieldNames[2] = "age";
//        fieldNames[3] = "op";
//        TypeInformation[] types = new TypeInformation[3];
//        types[0] = BasicTypeInfo.INT_TYPE_INFO;
//        types[1] = BasicTypeInfo.STRING_TYPE_INFO;
//        types[2] = BasicTypeInfo.INT_TYPE_INFO;
//        types[3] = BasicTypeInfo.STRING_TYPE_INFO;
//        RowTypeInfo typeInfo = new RowTypeInfo(types,fieldNames);
//        SingleOutputStreamOperator<Row> resStream = filter.map((MapFunction<Tuple2<Boolean, Row>, Row>) value -> {
//            Row row = value.f1;
////            int arity = row.getArity();
////            Object[] objects = new Object[arity];
////            for (int i = 0; i < arity; i++) {
////                objects[i] = row.getField(i);
////            }
////            objects[arity]=row.getKind().toString();
////            Row of = Row.of(objects);
////            of.setKind(row.getKind());
//            return row;
////            return Row.copy(row);
//        }).returns(typeInfo);
//        TypeInformation<Row> type = resStream.getType();
//        System.out.println(type.toString());
//        Schema schema = Schema.newBuilder().column("name", DataTypes.STRING()).column("age", DataTypes.INT()).build();
//        Table table2 = tableEvn.fromDataStream(resStream).as("name","age");
//        tableEvn.createTemporaryView("dd",resStream,$("id"),$("name"), $("age"),$("cdc_op"));
//        Table table1 = tableEvn.sqlQuery("select * from dd");
//        table1.printSchema();
//        tableEvn.toAppendStream(table1,Row.class).print("tableinfo:");

//        map.print("str:").setParallelism(1);
       /* SingleOutputStreamOperator<String> map = resStream.map(new MapFunction<Row, String>() {
            @Override
            public String map(Row value) throws Exception {
                String s = value.getField("id") + ":" + value.getField("name") + ":" + value.getField("op");
                return s;
            }
        });*/
//        map.print().setParallelism(1);
//        resStream.addSink(new SinkFunction<Row>() {
//            @Override
//            public void invoke(Row value, Context context) throws Exception {
//                System.out.println(value.getKind());
//                System.out.println(value.getField("id") + ":" + value.getField("name") + ":" + value.getField("age"));
////                System.out.println(value.getField("op"));
//            }
//        });
//        resStream.print("row:").setParallelism(1);
        env.execute();
    }
}
