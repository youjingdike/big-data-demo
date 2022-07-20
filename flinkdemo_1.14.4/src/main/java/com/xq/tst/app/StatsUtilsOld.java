package com.xq.tst.app;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.common.StatsSetupConst;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class StatsUtilsOld {

    public static long getNumRows(Table table) {
        return getBasicStatForTable(table, StatsSetupConst.ROW_COUNT);
    }

    public static long getDataSize(HiveConf conf,Table table) {
        long ds = getRawDataSize(table);
        if (ds <= 0) {
            ds = getTotalSize(table);

            // if data size is still 0 then get file size
            if (ds <= 0) {
                ds = getFileSizeForTable(conf, table);
            }
            float deserFactor =
                    HiveConf.getFloatVar(conf, HiveConf.ConfVars.HIVE_STATS_DESERIALIZATION_FACTOR);
            ds = (long) (ds * deserFactor);
        }

        return ds;
    }

    public static long getRawDataSize(Table table) {
        return getBasicStatForTable(table, StatsSetupConst.RAW_DATA_SIZE);
    }

    /**
     * Get total size of a give table
     * @return total size
     */
    public static long getTotalSize(Table table) {
        return getBasicStatForTable(table, StatsSetupConst.TOTAL_SIZE);
    }

    public static long getBasicStatForTable(Table table, String statType) {
        Map<String, String> params = table.getParameters();
        long result = 0;

        if (params != null) {
            try {
                result = Long.parseLong(params.get(statType));
            } catch (NumberFormatException e) {
                result = 0;
            }
        }
        return result;
    }

    public static long getFileSizeForTable(HiveConf conf, Table table) {
        long size = 0;
        String location = table.getSd().getLocation();

        if (location == null) {
            return size;
        }
        Path path = new Path(location);
        try {
            FileSystem fs = path.getFileSystem(conf);
            size = fs.getContentSummary(path).getLength();
        } catch (Exception e) {
            size = 0;
        }
        return size;
    }

    public boolean isPartitioned(Table table) {
        if (getPartCols(table) == null) {
            return false;
        }
        return (getPartCols(table).size() != 0);
    }

    public List<FieldSchema> getPartCols(Table table) {
        List<FieldSchema> partKeys = table.getPartitionKeys();
        if (partKeys == null) {
            partKeys = new ArrayList<FieldSchema>();
            table.setPartitionKeys(partKeys);
        }
        return partKeys;
    }
}
