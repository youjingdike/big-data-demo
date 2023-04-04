package com.xq.tst;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Properties;


public class AppWin {
    private static final Logger log = LoggerFactory.getLogger(AppWin.class);
    public static final String saslJaasConfig= "com.sun.security.auth.module.Krb5LoginModule required \n useKeyTab=true \n keyTab=\"{keytabPath}\" \n storeKey=true \n debug=true \n useTicketCache=false \n principal=\"{principal}\";";
    //参数常量
    private static final String PARALLELISM_ARGS = "parallelism";
    private static final String STATE_BACKEND_ARGS = "state.backend";
    private static final String SRC_TOPIC_ARGS = "src.topic";
    private static final String DST_TOPIC_ARGS = "dst.topic";

    private static final String GROUP_ID_ARGS = "group.id";

    private static final String BOOTSTRAP_SERVERS_ARGS = "bootstrap.servers";

    private static final String CHECKPOINT_PATH_ARGS = "ckp.path";
    private static final String CHECKPOINT_INTERVAL_ARGS = "ckp.interval";
    private static final String CHECKPOINT_TYPE_ARGS = "ckp.type";

    private static final String AUTO_OFFSET_RESET_ARGS = "auto.offset.reset";
    private static final String IS_KERBS_ARGS = "is.kerbs";
    private static final String KEYTAB_PATH_ARGS = "keytab.path";
    private static final String PRINCIPAL_ARGS = "principal";
    private static final String IS_USER_OP_ARGS = "is.user.op";
    private static final String WIN_TIME_ARGS = "win.time";
    private static final String IS_SLIDING_WIN_ARGS = "is.sliding.win";
    private static final String WIN_SLIDING_ARGS = "win.sliding";
    //参数值常量
    private static final String ROCKSDB_STATE_BACKEND = "rocksdb";
    private static final String AT_LEAST_ONCE = "at_least_once";
    private static String LATEST_OFFSET_RESET = "latest";
    private static final String SLIDING_WIN = "SlidingWin";
    private static final String TUMBLING_WIN = "TumblingWin";
    //参数变量及默认值
    private static boolean isKerbs = false;
    private static boolean isUserOp = false;
    private static Integer parallelism = 1;
    private static long ckpInterval = 10000L;
    private static String stateBackend = "hash";
    private static String srcTopic = "zx_x_src";
    private static String dstTopic = "zx_x_dst";

    private static String groupId = "flink-x-tst";

    private static String chkType = "exactly_once";

    private static String bootstrapServers = "XXXX";

    private static String checkpointDataUri = "hdfs://XXXX:8020/tmp/flink/ckp";

    private static String autoOffsetReset = "latest";
    private static String keytabPath = "/home/flink/kafka.service.keytab";
    private static String principal = "kafka/XXXX@HADOOP.COM";
    private static boolean isSlidingWin = false;
    private static long winTime = 60L;
    private static long winSliding = 30L;
    public static void main(String[] args) throws Exception {
        if (args.length != 0) {
//            Map<String, String> argsMap = fromArgs(args);
            ParameterTool argsMap = ParameterTool.fromArgs(args);
            if (argsMap.get(PARALLELISM_ARGS) != null)
                parallelism = Integer.parseInt(argsMap.get(PARALLELISM_ARGS));
            log.info("@@@@@parallelism: {}", parallelism);
            if (argsMap.get(CHECKPOINT_INTERVAL_ARGS) != null) {
                ckpInterval = Long.parseLong(argsMap.get(CHECKPOINT_INTERVAL_ARGS));
            }
            log.info("@@@@@ckpInterval: {}", ckpInterval);
            if (argsMap.get(SRC_TOPIC_ARGS) != null)
                srcTopic = argsMap.get(SRC_TOPIC_ARGS);
            log.info("@@@@@topic: {}", srcTopic);
            if (argsMap.get(STATE_BACKEND_ARGS) != null)
                stateBackend = argsMap.get(STATE_BACKEND_ARGS);
            log.info("@@@@@topic: {}", srcTopic);
            if (argsMap.get(DST_TOPIC_ARGS) != null)
                dstTopic = argsMap.get(DST_TOPIC_ARGS);
            log.info("@@@@@dst_topic: {}", dstTopic);
            if (argsMap.get(GROUP_ID_ARGS) != null)
                groupId = argsMap.get(GROUP_ID_ARGS);
            log.info("@@@@@group_id: {}", groupId);
            if (argsMap.get(BOOTSTRAP_SERVERS_ARGS) != null)
                bootstrapServers = argsMap.get(BOOTSTRAP_SERVERS_ARGS);
            log.info("@@@@@bootstrapServers: {}", bootstrapServers);
            if (argsMap.get(CHECKPOINT_PATH_ARGS) != null)
                checkpointDataUri = argsMap.get(CHECKPOINT_PATH_ARGS);
            log.info("@@@@@checkpointDataUri: {}", checkpointDataUri);
            if (argsMap.get(CHECKPOINT_TYPE_ARGS) != null)
                chkType = argsMap.get(CHECKPOINT_TYPE_ARGS);
            log.info("@@@@@chkType: {}", chkType);
            if (argsMap.get(AUTO_OFFSET_RESET_ARGS) != null)
                autoOffsetReset = argsMap.get(AUTO_OFFSET_RESET_ARGS);
            log.info("@@@@@autoOffsetReset: {}", autoOffsetReset);
            if (argsMap.get(IS_KERBS_ARGS) != null)
                isKerbs = Boolean.parseBoolean(argsMap.get(IS_KERBS_ARGS));
            log.info("@@@@@isKerbs: {}", isKerbs);
            if (argsMap.get(KEYTAB_PATH_ARGS) != null)
                keytabPath = argsMap.get(KEYTAB_PATH_ARGS);
            log.info("@@@@@keytabPath: {}", keytabPath);
            if (argsMap.get(PRINCIPAL_ARGS) != null)
                principal = argsMap.get(PRINCIPAL_ARGS);
            log.info("@@@@@principal: {}", principal);
            if (argsMap.get(IS_USER_OP_ARGS) != null)
                isUserOp = Boolean.parseBoolean(argsMap.get(IS_USER_OP_ARGS));
            log.info("@@@@@isUserOp: {}", isUserOp);
            if (argsMap.get(WIN_TIME_ARGS) != null)
                winTime = Long.parseLong(argsMap.get(WIN_TIME_ARGS));
            log.info("@@@@@winTime: {}", winTime);
            if (argsMap.get(WIN_SLIDING_ARGS) != null)
                winSliding = Long.parseLong(argsMap.get(WIN_SLIDING_ARGS));
            log.info("@@@@@winSliding: {}", winSliding);
            if (argsMap.get(IS_SLIDING_WIN_ARGS) != null)
                isSlidingWin = Boolean.parseBoolean(argsMap.get(IS_SLIDING_WIN_ARGS));
            log.info("@@@@@isSlidingWin: {}", isSlidingWin);
        }

        /*Configuration conf = new Configuration();
        conf.setBoolean(ConfigConstants.LOCAL_START_WEBSERVER,true);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);*/
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getCheckpointConfig().setCheckpointInterval(ckpInterval);
        if (AT_LEAST_ONCE.equalsIgnoreCase(chkType)) {
            env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.AT_LEAST_ONCE);
        } else {
            env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        }
        env.setParallelism(parallelism);
        if (ROCKSDB_STATE_BACKEND.equalsIgnoreCase(stateBackend)) {
            env.setStateBackend(new RocksDBStateBackend(checkpointDataUri));
        } else {
            env.setStateBackend(new MemoryStateBackend());
        }
//        env.getCheckpointConfig().setCheckpointStorage(new FileSystemCheckpointStorage(checkpointDataUri));
        Properties srcProperties = new Properties();
        srcProperties.setProperty("bootstrap.servers", bootstrapServers);
        srcProperties.setProperty("group.id", groupId);
//        srcProperties.setProperty("client.id", "11111");
        if (LATEST_OFFSET_RESET.equalsIgnoreCase(autoOffsetReset)) {
            srcProperties.setProperty("auto.offset.reset", "latest");
        } else {
            srcProperties.setProperty("auto.offset.reset", "earliest");
        }
        if (isKerbs) {
            srcProperties.setProperty("security.protocol", "SASL_PLAINTEXT");
            srcProperties.setProperty("sasl.mechanism", "GSSAPI");
            srcProperties.setProperty("sasl.kerberos.service.name", "kafka");
            srcProperties.setProperty("sasl.jaas.config", saslJaasConfig.replace("{keytabPath}", keytabPath)
                    .replace("{principal}", principal));
//            kafkaSinkBuilder.setKafkaProducerConfig(properties);
        }

        SingleOutputStreamOperator<String> inputStream = env.addSource(new FlinkKafkaConsumer<>(srcTopic, new SimpleStringSchema(), srcProperties))
                .name("kakfa source");

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", bootstrapServers);
//        properties.put("transaction.timeout.ms", 15 * 60 * 1000);
        if (isKerbs) {
            properties.setProperty("security.protocol", "SASL_PLAINTEXT");
            properties.setProperty("sasl.mechanism", "GSSAPI");
            properties.setProperty("sasl.kerberos.service.name", "kafka");
            properties.setProperty("sasl.jaas.config", saslJaasConfig.replace("{keytabPath}", keytabPath)
                            .replace("{principal}", principal));
//            kafkaSinkBuilder.setKafkaProducerConfig(properties);
        }

        FlinkKafkaProducer<String> myProducer = new FlinkKafkaProducer<>(
                dstTopic,// 目标 topic
                new SimpleStringSchema(),     // 序列化 schema
                properties                  // producer 配置
                );

        if (isUserOp) {
            SingleOutputStreamOperator<String> map = inputStream.map((MapFunction<String, String>) value -> value == null ? null : value + "@kafka2kafka");
            KeyedStream<String, String> keyedStream = map.keyBy((KeySelector<String, String>) value -> value);
            SingleOutputStreamOperator<String> process = null;
            if (isSlidingWin) {
                process = keyedStream.window(SlidingProcessingTimeWindows.of(Time.seconds(winTime), Time.seconds(winSliding)))
                        .process(new MyProcessWindowFunction(SLIDING_WIN));
            } else {
                process = keyedStream.window(TumblingProcessingTimeWindows.of(Time.seconds(winTime)))
                        .process(new MyProcessWindowFunction(TUMBLING_WIN));
            }
            process.print();
            process.addSink(myProducer).name("kfkSink").uid("kfkSink");
        } else {
            KeyedStream<String, String> keyedStream = inputStream.keyBy((KeySelector<String, String>) value -> value);
            SingleOutputStreamOperator<String> process = null;
            if (isSlidingWin) {
                process = keyedStream.window(SlidingProcessingTimeWindows.of(Time.seconds(winTime), Time.seconds(winSliding)))
                        .process(new MyProcessWindowFunction(SLIDING_WIN));
            } else {
                process = keyedStream.window(TumblingProcessingTimeWindows.of(Time.seconds(winTime)))
                        .process(new MyProcessWindowFunction(TUMBLING_WIN));
            }

            process.print();
            process.addSink(myProducer).name("kfkSink").uid("kfkSink");
        }
        env.execute("test kafka source and sink win job");
    }

    /*public static Map<String, String> fromArgs(String[] args) {
        Map<String, String> propMap = new HashMap<>(args.length / 2);
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-") && i != args.length - 1)
                propMap.put(args[i], args[i + 1]);
        }
        return propMap;
    }*/
    private static class MyProcessWindowFunction
            extends ProcessWindowFunction<String, String, String, TimeWindow> {
        private String winType = "";

        public MyProcessWindowFunction(String winType) {
            this.winType = winType;
        }

        @Override
        public void process(String key, ProcessWindowFunction<String, String, String, TimeWindow>.Context context, Iterable<String> elements, Collector<String> out) throws Exception {
            long count = 0;
            for (String element : elements) {
                count++;
            }
            out.collect(winType+": " + context.window() + "->key: "+key+",count: " + count);
        }
    }
}
