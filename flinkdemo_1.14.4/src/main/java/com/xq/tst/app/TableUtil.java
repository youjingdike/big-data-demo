package com.xq.tst.app;

import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.Table;

import java.util.ArrayList;
import java.util.List;

public class TableUtil {
    public boolean isPartitioned(Table table) {
        if (getPartCols(table) == null) {
            return false;
        }
        return (getPartCols(table).size() != 0);
    }

    private List<FieldSchema> getPartCols(Table table) {
        List<FieldSchema> partKeys = table.getPartitionKeys();
        if (partKeys == null) {
            partKeys = new ArrayList<FieldSchema>();
            table.setPartitionKeys(partKeys);
        }
        return partKeys;
    }
}
