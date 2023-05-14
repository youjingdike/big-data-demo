package com.ks.tst;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.runtime.state.storage.FileSystemCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Hb2hb {
    private static final Logger log = LoggerFactory.getLogger(Hb2hb.class);
    //参数常量
    private static final String PARALLELISM_ARGS = "parallelism";
    private static final String STATE_BACKEND_ARGS = "state.backend";
    private static final String CHECKPOINT_PATH_ARGS = "ckp.path";
    private static final String CHECKPOINT_INTERVAL_ARGS = "ckp.interval";
    private static final String CHECKPOINT_TYPE_ARGS = "ckp.type";

    private static final String IS_KERBS_ARGS = "is.kerbs";
    private static final String HBASE_KEYTAB_PATH_ARGS = "hbase.keytab.path";
    private static final String HBASE_PRINCIPAL_ARGS = "hbase.principal";
    private static final String HBASE_ZK = "hbase.zk";
    private static final String HBASE_ZNODE = "hbase.znode";

    //参数值常量
    private static final String ROCKSDB_STATE_BACKEND = "rocksdb";
    private static final String AT_LEAST_ONCE = "at_least_once";
    //参数变量及默认值
    private static boolean isKerbs = false;
    private static Integer parallelism = 1;
    private static long ckpInterval = 10000L;
    private static String stateBackend = "hash";

    private static String chkType = "exactly_once";

    private static String checkpointDataUri = "hdfs://XXXX:8020/tmp/flink/ckp";

    private static String hbaseKeytabPath = "/home/flink/hbase.client.keytab";
    private static String hbasePrincipal = "hbase/XXXX@HADOOP.COM";
    private static String hbaseZk = "";
    private static String zNode = "/hbase-secure";

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
            if (argsMap.get(STATE_BACKEND_ARGS) != null)
                stateBackend = argsMap.get(STATE_BACKEND_ARGS);
            log.info("@@@@@stateBackend: {}", stateBackend);
            if (argsMap.get(CHECKPOINT_PATH_ARGS) != null)
                checkpointDataUri = argsMap.get(CHECKPOINT_PATH_ARGS);
            log.info("@@@@@checkpointDataUri: {}", checkpointDataUri);
            if (argsMap.get(CHECKPOINT_TYPE_ARGS) != null)
                chkType = argsMap.get(CHECKPOINT_TYPE_ARGS);
            log.info("@@@@@chkType: {}", chkType);
            if (argsMap.get(IS_KERBS_ARGS) != null)
                isKerbs = Boolean.parseBoolean(argsMap.get(IS_KERBS_ARGS));
            log.info("@@@@@isKerbs: {}", isKerbs);
            if (argsMap.get(HBASE_ZK) != null)
                hbaseZk = argsMap.get(HBASE_ZK);
            log.info("@@@@@hbase_zk: {}", hbaseZk);
            if (argsMap.get(HBASE_KEYTAB_PATH_ARGS) != null)
                hbaseKeytabPath = argsMap.get(HBASE_KEYTAB_PATH_ARGS);
            log.info("@@@@@hbaseKeytabPath: {}", hbaseKeytabPath);
            if (argsMap.get(HBASE_PRINCIPAL_ARGS) != null)
                hbasePrincipal = argsMap.get(HBASE_PRINCIPAL_ARGS);
            log.info("@@@@@hbasePrincipal: {}", hbasePrincipal);
            if (argsMap.get(HBASE_ZNODE) != null)
                zNode = argsMap.get(HBASE_ZNODE);
            log.info("@@@@@znode: {}", zNode);
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

        /*start 创建hbase表*/
        String hbaseSrcDDl = "create table pInfo( \n" +
                " rowkey STRING,\n" +
                " f ROW<name STRING,age INT>,\n" +
                " PRIMARY KEY (rowkey) NOT ENFORCED \n" +
                ") WITH ( \n" +
                " 'connector' = 'hbase-2.2',\n" +
                " 'table-name' = 'xyPoc:pInfo',\n" +
                " 'zookeeper.znode.parent' = '" + zNode + "',\n" +
                " 'zookeeper.quorum' = '"+ hbaseZk +"'";
        if (isKerbs) {
            hbaseSrcDDl += ", \n" +
                    " 'properties.hbase.security.authentication' = 'kerberos',\n" +
                    " 'properties.hadoop.security.authentication' = 'kerberos',\n" +
                    " 'properties.kerberos.keytab' = '"+hbaseKeytabPath+"',\n" +
                    " 'properties.hbase.client.keytab.file' = '"+hbaseKeytabPath+"',\n" +
                    " 'properties.hbase.master.kerberos.principal' = '"+hbasePrincipal+"',\n" +
                    " 'properties.hbase.client.keytab.principal' = '"+hbasePrincipal+"',\n" +
                    " 'properties.hbase.regionserver.kerberos.principal' = '"+hbasePrincipal+"'\n";

        }
        hbaseSrcDDl += ")";
        System.out.println(hbaseSrcDDl);
        tableEnv.executeSql(hbaseSrcDDl);

        String hbaseSinkDDl = "create table pInfoNew( \n" +
                " rowkey STRING,\n" +
                " f ROW<name STRING,age INT>,\n" +
                " PRIMARY KEY (rowkey) NOT ENFORCED \n" +
                ") WITH ( \n" +
                " 'connector' = 'hbase-2.2',\n" +
                " 'table-name' = 'xyPoc:pInfoNew',\n" +
                " 'zookeeper.znode.parent' = '" + zNode + "',\n" +
                " 'zookeeper.quorum' = '"+ hbaseZk +"'";
        if (isKerbs) {
            hbaseSinkDDl += ",\n" +
                    " 'properties.hbase.security.authentication' = 'kerberos',\n" +
                    " 'properties.hadoop.security.authentication' = 'kerberos',\n" +
                    " 'properties.kerberos.keytab' = '"+hbaseKeytabPath+"',\n" +
                    " 'properties.hbase.client.keytab.file' = '"+hbaseKeytabPath+"',\n" +
                    " 'properties.hbase.master.kerberos.principal' = '"+hbasePrincipal+"',\n" +
                    " 'properties.hbase.client.keytab.principal' = '"+hbasePrincipal+"',\n" +
                    " 'properties.hbase.regionserver.kerberos.principal' = '"+hbasePrincipal+"'\n";
        }
        hbaseSinkDDl += ")";
        tableEnv.executeSql(hbaseSinkDDl);
        /*end 创建hbase表*/

        //进行维表关联查询
        String forHbsql = "select rowkey,ROW(cast((f.name || '.ks') as string),f.age) from pInfo";
        /*String forHbsql = "select rowkey,ROW(name,age) from \n" +
                " (select rowkey,(p.f.name || '.ks') as name,p.f.age as age from pInfo p)";*/
        Table tableHb = tableEnv.sqlQuery(forHbsql);

        tableHb.executeInsert("pInfoNew");

        tableEnv.toDataStream(tableHb).print();

        env.execute("Hb2hbTst");
    }
}
