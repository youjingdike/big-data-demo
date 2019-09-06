package com.xq.tst;

public class TableInfo {
    private String dataType;
    private String columnType;
    private String hiveColumnName;
    private String hbaseColumnName;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getHiveColumnName() {
        return hiveColumnName;
    }

    public void setHiveColumnName(String hiveColumnName) {
        this.hiveColumnName = hiveColumnName;
    }

    public String getHbaseColumnName() {
        return hbaseColumnName;
    }

    public void setHbaseColumnName(String hbaseColumnName) {
        this.hbaseColumnName = hbaseColumnName;
    }
}
