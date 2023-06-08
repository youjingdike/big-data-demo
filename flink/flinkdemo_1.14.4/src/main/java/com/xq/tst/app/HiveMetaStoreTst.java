package com.xq.tst.app;

import com.google.common.collect.Lists;
import com.google.common.math.LongMath;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.common.StatsSetupConst;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.IMetaStoreClient;
import org.apache.hadoop.hive.metastore.RetryingMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.AggrStats;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsObj;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.ql.exec.ColumnInfo;
import org.apache.hadoop.hive.ql.metadata.Hive;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.parse.PrunedPartitionList;
import org.apache.hadoop.hive.ql.plan.ColStatistics;
import org.apache.hadoop.hive.ql.util.JavaDataModel;
import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StandardConstantListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StandardConstantMapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StandardConstantStructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StandardListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StandardMapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.UnionObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.BinaryObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.HiveCharObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.HiveVarcharObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableBinaryObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableBooleanObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableByteObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableDateObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableDoubleObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableFloatObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableHiveDecimalObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableIntObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableLongObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableShortObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableStringObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableTimestampObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.CharTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.VarcharTypeInfo;
import org.apache.hadoop.io.BytesWritable;
import org.apache.thrift.TException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
public class HiveMetaStoreTst
{
    public static void main( String[] args ) throws Exception {



        String db = "db";
        String tableName = "tableName";

        getSize(db, tableName);
    }

    private static long getSize(String db, String tableName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, TException {
        String totalSize = "totalSize";
        String rawDataSize = "rawDataSize";
        HiveConf hiveConf = new HiveConf();
        Method method = RetryingMetaStoreClient.class.getMethod("getProxy", HiveConf.class, boolean.class);
        IMetaStoreClient client = (IMetaStoreClient) method.invoke(null, hiveConf, true);
        //        HiveMetaStoreClient hiveMetaStoreClient = new HiveMetaStoreClient(hiveConf);
        //        hiveMetaStoreClient.getPartitionColumnStatistics();

        Table table = client.getTable(db, tableName);
        float deserFactor = HiveConf.getFloatVar(hiveConf, HiveConf.ConfVars.HIVE_STATS_DESERIALIZATION_FACTOR);

        List<FieldSchema> partitionKeys = table.getPartitionKeys();
        if (partitionKeys==null) {
            partitionKeys = new ArrayList<>();
            table.setPartitionKeys(partitionKeys);
        }
        long ds = 0;

        if (partitionKeys.size() != 0) {
            List<Long> dataSizes;

            List<Partition> partitions = client.listPartitions(db, tableName, (short) -1);
            dataSizes = getBasicStatForPartitions(partitions,rawDataSize);
            ds = getSumIgnoreNegatives(dataSizes);
            if (ds <= 0) {
                dataSizes = getBasicStatForPartitions(partitions,totalSize);
                ds = getSumIgnoreNegatives(dataSizes);
            }
            ds = (long) (ds * deserFactor);
        } else {
            try {
                ds = Long.parseLong(table.getParameters().get(rawDataSize));
            } catch (NumberFormatException e) {
                ds = 0;
            }

            if (ds <= 0) {
                try {
                    ds = Long.parseLong(table.getParameters().get(totalSize));
                } catch (NumberFormatException e) {
                    ds = 0;
                }
            }
        }

        return ds;
    }

    public static List<Long> getBasicStatForPartitions(List<Partition> parts,
                                                       String statType) {

        List<Long> stats = Lists.newArrayList();
        for (Partition part : parts) {
            Map<String, String> params = part.getParameters();
            long result = 0;
            if (params != null) {
                try {
                    result = Long.parseLong(params.get(statType));
                } catch (NumberFormatException e) {
                    result = 0;
                }
                stats.add(result);
            }
        }
        return stats;
    }

    public static long getSumIgnoreNegatives(List<Long> vals) {
        long result = 0;
        for (Long l : vals) {
            if (l > 0) {
                result = safeAdd(result, l);
            }
        }
        return result;
    }

    public static long safeAdd(long a, long b) {
        try {
            return LongMath.checkedAdd(a, b);
        } catch (ArithmeticException ex) {
            return Long.MAX_VALUE;
        }
    }

}
