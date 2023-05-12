package com.ks.tst;

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


public class Kfk2more {
    private static final Logger log = LoggerFactory.getLogger(Kfk2more.class);
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
    private static final String HIVE_CONF_DIR = "hive.conf";
    private static final String HDFS_PATH = "hdfs.path";
    private static final String IS_USER_OP_ARGS = "is.user.op";

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
    private static String hiveConfDir    = "./hive-conf";
    private static String hdfsPath = "hdfs:///user/flink/p_info";
    public static void main(String[] args) throws Exception {
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
                        .column("id", DataTypes.BIGINT())
                        .column("name", DataTypes.STRING())
                        .column("age", DataTypes.INT())
                        .column("dptId", DataTypes.INT())
                        .column("addrId", DataTypes.INT())
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

        /*start 创建hbase表*/
        String hbaseSrcDDl = "create table dptInfo( \n" +
                " rowkey STRING,\n" +
                " f ROW<dptName STRING>,\n" +
                " PRIMARY KEY (rowkey) NOT ENFORCED \n" +
                ") WITH ( \n" +
                " 'connector' = 'hbase-2.2',\n" +
                " 'table-name' = 'xyPoc:dptInfo',\n" +
                " 'zookeeper.quorum' = '"+hbase_zk+"'";
        if (isKerbs) {
            hbaseSrcDDl += ",\n" +
                    " 'hbase.client.keytab.file' = '"+hbaseKeytabPath+"',\n" +
                    " 'hbase.client.keytab.principal' = '"+hbasePrincipal+"'\n";

        }
        hbaseSrcDDl += ")";
        tableEnv.executeSql(hbaseSrcDDl);

        String hbaseSinkDDl = "create table pInfo( \n" +
                " rowkey STRING,\n" +
                " f ROW<name STRING,age INT,dptName STRING,addr STRING>,\n" +
                " PRIMARY KEY (rowkey) NOT ENFORCED \n" +
                ") WITH ( \n" +
                " 'connector' = 'hbase-2.2',\n" +
                " 'table-name' = 'xyPoc:pInfo',\n" +
                " 'zookeeper.quorum' = '"+hbase_zk+"'";
        if (isKerbs) {
            hbaseSinkDDl += ",\n" +
                    " 'hbase.client.keytab.file' = '"+hbaseKeytabPath+"',\n" +
                    " 'hbase.client.keytab.principal' = '"+hbasePrincipal+"'\n";
        }
        hbaseSinkDDl += ")";
        tableEnv.executeSql(hbaseSinkDDl);
        /*end 创建hbase表*/

        /*start 创建hdfs表*/
        String hdfsDDL = "CREATE TABLE fs_table (\n" +
                " id BIGINT,\n" +
                " name STRING\n" +
                " age INT,\n" +
                " dptName STRING\n" +
                " addr STRING\n" +
                ") WITH (\n" +
                "  'connector'='filesystem',\n" +
                "  'path'='"+ hdfsPath +"',\n" +
                "  'format'='csv',\n" +
                "  'field-delimiter'=',',\n" +
                "  'sink.partition-commit.delay'='1 m',\n" +
                "  'sink.partition-commit.policy.kind'='success-file'\n" +
                ")";
        tableEnv.executeSql(hdfsDDL);
        /*end 创建hdfs表*/

        /*start 创建hive维表*/
        String name            = "myHive";
        String defaultDatabase = "xyPoc";

        HiveCatalog hive = new HiveCatalog(name, defaultDatabase, hiveConfDir);
        tableEnv.registerCatalog("myHive", hive);
        tableEnv.useCatalog("myHive");
        // to use hive dialect
        tableEnv.getConfig().setSqlDialect(SqlDialect.HIVE);

        String hiveSrcDDL = "create table addrInfo(\n" +
                " id BIGINT,\n" +
                " addr STRING\n" +
                ") TBLPROPERTIES ( \n" +
                "  'streaming-source.enable' = 'false',\n" +
                "  'streaming-source.partition.include' = 'all',\n" +
                "  'lookup.join.cache.ttl' = '12 h'\n" +
                ")";
        tableEnv.executeSql(hiveSrcDDL);
        String hiveSinkDDL = "create table pInfo(\n" +
                " id BIGINT,\n" +
                " name STRING\n" +
                " age INT,\n" +
                " dptName STRING\n" +
                " addr STRING\n" +
                ") STORED AS parquet TBLPROPERTIES  ( \n" +
                "  'sink.partition-commit.trigger'='partition-time',\n" +
                "  'sink.partition-commit.delay'='1 h',\n" +
                "  'sink.partition-commit.policy.kind'='metastore'" +
                ")";
        tableEnv.executeSql(hiveSinkDDL);
        /*end 创建hive维表*/

        // to use default dialect
        tableEnv.getConfig().setSqlDialect(SqlDialect.DEFAULT);

        tableEnv.useCatalog("default_catalog");

        //进行维表关联查询
        String sql = "select id,name,age,d.f.dptName as dptName,c.addr from people p \n" +
                " left join dptInfo d on p.dptId = d.rowkey \n" +
                " left join myHive.xyPoc.addrInfo FOR SYSTEM_TIME AS OF p.proctime AS c on c.id = p.addrId";
        Table table = tableEnv.sqlQuery(sql);
        String forHbsql = "select id as rowkey,ROW(name,age,d.f.dptName as dptName,c.addr) from people p \n" +
                " left join dptInfo d on p.dptId = d.rowkey \n" +
                " left join myHive.xyPoc.addrInfo FOR SYSTEM_TIME AS OF p.proctime AS c on c.id = p.addrId";
        /*String forHbsql = "select rowkey,ROW(name,age,dptName,addr) from (select id as rowkey,name,age,d.f.dptName as dptName,c.addr from people p \n" +
                " left join dptInfo d on p.dptId = d.rowkey \n" +
                " left join myHive.xyPoc.addrInfo FOR SYSTEM_TIME AS OF p.proctime AS c on c.id = p.addrId)";*/
        Table tableHb = tableEnv.sqlQuery(forHbsql);

        //构建sink
        table.executeInsert("myHive.xyPoc.pInfo");
        table.executeInsert("fs_table");

        tableHb.executeInsert("pInfo");

        tableEnv.toDataStream(table).print("table:");
        tableEnv.toDataStream(tableHb).print("tableHb:");

        env.execute("kfk2moreTst");
    }
}
