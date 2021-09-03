/*
package org.apache.flink.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.RetryingMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

public class HiveCatalog {
    private static final Logger LOG = LoggerFactory.getLogger(HiveCatalog.class);
    private IMetaStoreClient client;
    private Table table;
    private Boolean addPartitionPolicy;
    private long addPartitionTimeout;
    private int addPartitionRetryNum;
    private static ThreadPoolExecutor asyncExecutor = RetryUtil.createThreadPoolExecutor();

    public HiveCatalog(IMetaStoreClient client, Table table, Boolean addPartitionPolicy
            , long addPartitionTimeout, Integer addPartitionRetryNum) {
        this.client = client;
        this.table = table;
        this.addPartitionPolicy = addPartitionPolicy;
        this.addPartitionRetryNum = addPartitionRetryNum;
        this.addPartitionTimeout = addPartitionTimeout;
    }

    public static HiveConf createHiveConf(@Nullable String hiveConfDir) {
        LOG.info("Setting hive conf dir as {}", hiveConfDir);
        try {
            HiveConf.setHiveSiteLocation(
                    hiveConfDir == null ?
                            null : Paths.get(hiveConfDir, "hive-site.xml").toUri().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(String.format("Failed to get hive-site.xml from %s", hiveConfDir), e);
        }
        return new HiveConf();
    }

    public static IMetaStoreClient getHiveMetastoreClient(HiveConf hiveConf) {
        try {
            Method method = RetryingMetaStoreClient.class.getMethod("getProxy", Configuration.class, Boolean.TYPE);
            // getProxy is a static method
            return (IMetaStoreClient) method.invoke(null, hiveConf, true);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create Hive Metastore client", ex);
        }
    }

    public String getLocation() {
        return table.getSd().getLocation();
    }

    public String getOutputFormat() {
        return table.getSd().getOutputFormat();
    }

    public String getFieldDelimiter() {
        return table.getSd().getSerdeInfo().getParameters().get("field.delim");
    }

    public boolean isPartitionTable() {
        return table.getPartitionKeysSize() > 0;
    }

    // load a single partition
    public void loadPartition(List<String> partitions, String destPath) throws TException {
        Boolean partitionExist = isPartitionExist(partitions);
        // register new partition if it doesn't exist
        if (!partitionExist) {
            registerPartition(partitions, destPath);
        }
    }


    // 判断 Partition 是否存在，原flink格式的 Partition
    public Boolean isPartitionExist(List<String> partitions) throws TException {
        List<Partition> existingPart = client.listPartitions(table.getDbName(), table.getTableName(), partitions, (short) 1);
        if (existingPart.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }


    // 判断 Partition 是否存在，原flink格式的 Partition
    public Boolean isStreamPathExist(List<String> partitions, String dataPath) throws TException {
        List<Partition> existingPart = client.listPartitions(table.getDbName(), table.getTableName(), partitions, (short) 1);
        if (existingPart.isEmpty()) {
            return false;
        } else {
            for (Partition partition : existingPart) {
                String path = partition.getSd().getLocation();
                LOG.debug("Partition path :" + path);
                if (path.contains(dataPath)) {
                    return false;
                }
            }
            return true;
        }
    }

    // 注册Partition分区
    public void registerPartition(List<String> partitions, String destPath) {

        StorageDescriptor sd = new StorageDescriptor(table.getSd());
        sd.setLocation(sd.getLocation() + File.separator + destPath);
        Partition partition = createHivePartition(table.getDbName(), table.getTableName(),
                partitions, sd, new HashMap<>());
        List<Partition> pas = new ArrayList();
        pas.add(partition);


        try {
            //Todo hive添加分区分为同步和异步两种方式
            if (!addPartitionPolicy) {
                RetryUtil.asyncExecuteWithRetry(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        long startTime = System.currentTimeMillis();
                        client.add_partitions(pas, true, false);
                        LOG.info("async add_partitions spend time:" + (System.currentTimeMillis() - startTime));
                        return true;
                    }
                }, addPartitionRetryNum, 2000L, true, addPartitionTimeout, asyncExecutor);
            } else {
                RetryUtil.executeWithRetry(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        long startTime = System.currentTimeMillis();
                        client.add_partitions(pas, true, false);
                        LOG.info("sync add_partitions spend time:" + (System.currentTimeMillis() - startTime));
                        return true;
                    }
                }, addPartitionRetryNum, 2000L, true);
            }
        } catch (SQLException e) {
            LOG.warn(String.format("after retry %s times, loadPartition meet a exception: ", 3), e);
            LOG.info("try to re execute for each record...");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    */
/**
     * Creates a Hive partition instance.
     *//*

    private Partition createHivePartition(String dbName, String tableName, List<String> values,
                                          StorageDescriptor sd, Map<String, String> parameters) {
        Partition partition = new Partition();
        partition.setDbName(dbName);
        partition.setTableName(tableName);
        partition.setValues(values);
        partition.setParameters(parameters);
        partition.setSd(sd);
        int currentTime = (int) (System.currentTimeMillis() / 1000);
        partition.setCreateTime(currentTime);
        partition.setLastAccessTime(currentTime);
        return partition;
    }
}
*/
