package com.xq.tst;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.runtime.state.storage.FileSystemCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.*;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.hive.HiveCatalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Kfk4hive2hive {
    private static final Logger log = LoggerFactory.getLogger(Kfk4hive2hive.class);
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
    private static final String HBASE_KEYTAB_PATH_ARGS = "hbase.keytab.path";
    private static final String HBASE_PRINCIPAL_ARGS = "hbase.principal";
    private static final String HBASE_ZK = "hbase.zk";
    private static final String HBASE_ZNODE = "hbase.znode";
    private static final String HIVE_CONF_DIR = "hive.conf";
    private static final String HDFS_PATH = "hdfs.path";
    private static final String IS_USER_OP_ARGS = "is.user.op";
    private static final String IS_HDFS = "is.hdfs";

    //参数值常量
    private static final String ROCKSDB_STATE_BACKEND = "rocksdb";
    private static final String AT_LEAST_ONCE = "at_least_once";
    private static String LATEST_OFFSET_RESET = "latest";

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
    private static String hbaseKeytabPath = "/home/flink/hbase.client.keytab";
    private static String hbasePrincipal = "hbase/XXXX@HADOOP.COM";
    private static String hbase_zk = "";
    private static String zNode = "/hbase-secure";
    private static String hiveConfDir    = "./hive-conf";
    private static String hdfsPath = "hdfs:///user/flink/p_info";

    private static boolean isHdfs = false;
    public static void main(String[] args) {
        if (args.length != 0) {
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
            log.info("@@@@@stateBackend: {}", srcTopic);
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
            if (argsMap.get(HBASE_ZK) != null)
                hbase_zk = argsMap.get(HBASE_ZK);
            log.info("@@@@@hbase_zk: {}", hbase_zk);
            if (argsMap.get(HIVE_CONF_DIR) != null)
                hiveConfDir = argsMap.get(HIVE_CONF_DIR);
            log.info("@@@@@hiveConfDir: {}", hiveConfDir);
            if (argsMap.get(HDFS_PATH) != null)
                hdfsPath = argsMap.get(HDFS_PATH);
            log.info("@@@@@hdfsPath: {}", hdfsPath);
            if (argsMap.get(HBASE_KEYTAB_PATH_ARGS) != null)
                hbaseKeytabPath = argsMap.get(HBASE_KEYTAB_PATH_ARGS);
            log.info("@@@@@hbaseKeytabPath: {}", hbaseKeytabPath);
            if (argsMap.get(HBASE_PRINCIPAL_ARGS) != null)
                hbasePrincipal = argsMap.get(HBASE_PRINCIPAL_ARGS);
            log.info("@@@@@hbasePrincipal: {}", hbasePrincipal);
            if (argsMap.get(IS_USER_OP_ARGS) != null)
                isUserOp = Boolean.parseBoolean(argsMap.get(IS_USER_OP_ARGS));
            log.info("@@@@@isUserOp: {}", isUserOp);
            if (argsMap.get(HBASE_ZNODE) != null)
                zNode = argsMap.get(HBASE_ZNODE);
            log.info("@@@@@znode: {}", zNode);
            if (argsMap.get(IS_HDFS) != null)
                isHdfs = Boolean.parseBoolean(argsMap.get(IS_HDFS));
            log.info("@@@@@isHdfs: {}", isHdfs);
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
            env.setStateBackend(new EmbeddedRocksDBStateBackend());
        } else {
            env.setStateBackend(new HashMapStateBackend());
        }
        env.getCheckpointConfig().setCheckpointStorage(new FileSystemCheckpointStorage(checkpointDataUri));
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        /*start 创建kafka源表*/
        TableDescriptor.Builder kafkaBuilder = TableDescriptor.forConnector("kafka")
                .schema(Schema.newBuilder()
                        .column("id", DataTypes.STRING())
                        .column("name", DataTypes.STRING())
                        .column("age", DataTypes.STRING())
                        .column("addrId", DataTypes.STRING())
                        .columnByExpression("proctime","PROCTIME()")
                        .build())
                .option("topic", srcTopic)
                .option("properties.bootstrap.servers", bootstrapServers)
                .option("properties.group.id", groupId)
                .format(FormatDescriptor.forFormat("csv").option("field-delimiter", ",").build());
        if (LATEST_OFFSET_RESET.equalsIgnoreCase(autoOffsetReset)) {
            kafkaBuilder.option("scan.startup.mode","latest-offset");
        } else {
            kafkaBuilder.option("scan.startup.mode","earliest-offset");
        }
        if (isKerbs) {
            kafkaBuilder.option("properties.security.protocol", "SASL_PLAINTEXT")
                    .option("properties.sasl.mechanism", "GSSAPI")
                    .option("properties.sasl.kerberos.service.name", "kafka")
                    .option("properties.sasl.jaas.config", saslJaasConfig.replace("{keytabPath}", keytabPath)
                            .replace("{principal}", principal));
        }
        final TableDescriptor sourceDescriptor = kafkaBuilder.build();
        tableEnv.createTemporaryTable("people",sourceDescriptor);
        /*end 创建kafka源表*/
        /*start 创建hive维表*/
        String name            = "myHive";
        String defaultDatabase = "xyPoc";

        HiveCatalog hive = new HiveCatalog(name, defaultDatabase, hiveConfDir,"3.1.0");
        tableEnv.registerCatalog("myHive", hive);

        //进行维表关联查询
        String sql = "select p.id as id,name,age,'' as dptName,c.addr from people p \n" +
                " left join myHive.xyPoc.addrInfo /*+ OPTIONS('streaming-source.enable' = 'false','streaming-source.partition.include' = 'all','lookup.join.cache.ttl' = '12 h') */ FOR SYSTEM_TIME AS OF p.proctime AS c on c.id = p.addrId";
        Table table = tableEnv.sqlQuery(sql);
        tableEnv.createTemporaryView("allInfo",table);

        //构建sink
        String hiveSinkSql = "insert into myHive.xyPoc.pInfo /*+ OPTIONS('sink.partition-commit.trigger'='partition-time','sink.partition-commit.delay'='1 h','sink.partition-commit.policy.kind'='metastore') */ \n" +
                " select * from allInfo";
        tableEnv.executeSql(hiveSinkSql);

        tableEnv.toDataStream(table).print("table:");

//        env.execute("kfk2moreTst");
    }
}
