package org.apache.flink.streaming.api.functions.sink.xq;

import java.util.ArrayList;
import java.util.List;

public class BucketStateInfo {
    private String tableName;
    private List<BucketState> stateList = new ArrayList<>();

    public BucketStateInfo(String tableName) {
        this.tableName = tableName;
    }

    public BucketStateInfo(String tableName, List<BucketState> stateList) {
        this.tableName = tableName;
        this.stateList = stateList;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<BucketState> getStateList() {
        return stateList;
    }

    public void setStateList(List<BucketState> stateList) {
        this.stateList = stateList;
    }
}
