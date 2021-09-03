package org.apache.flink.streaming.api.functions.sink.xq;

import java.util.List;

public class MaxPartCounterStateInfo {
    private String tableName;
    private Long maxPartCounter;

    public MaxPartCounterStateInfo(String tableName, Long maxPartCounter) {
        this.tableName = tableName;
        this.maxPartCounter = maxPartCounter;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getMaxPartCounter() {
        return maxPartCounter;
    }

    public void setMaxPartCounter(Long maxPartCounter) {
        this.maxPartCounter = maxPartCounter;
    }
}
