package com.xq.tst;

import org.apache.commons.lang.StringUtils;

import java.util.List;

public class HiveUtils {

    public static String getHiveSql(String hiveHbaseTableName,String partitionField,String hiveDelimiter, List<TableInfo> list) {
        StringBuilder sb = new StringBuilder("create table if not exists ");
        //表名
        sb.append(hiveHbaseTableName);
        //拼接字段
        sb.append(" （key string,");
        for (TableInfo tableInfo : list) {
            sb.append(tableInfo.getHiveColumnName());
            sb.append(" ");
            sb.append(getHiveDataType(tableInfo.getDataType(), tableInfo.getColumnType()));
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);
        //拼接分区
        sb.append(")partition by (");
        sb.append(partitionField);
        sb.append(" string) ROW FORMAT DELIMITED FIELDS TERMINATED BY '");
        //拼接分隔符
        if (StringUtils.isBlank(hiveDelimiter)) {
            sb.append(",");
        } else {
            sb.append(hiveDelimiter);
        }
        sb.append("' stored as orc");
        return sb.toString();
    }

    public static String getHbaseWithHiveSql(String hiveHbaseTableName,String hbaseTableName, List<TableInfo> list) {
        StringBuilder sb = new StringBuilder("create external table if not exists ");
        //表名
        sb.append(hiveHbaseTableName);
        //拼接字段
        sb.append("(key string,");
        for (TableInfo tableInfo : list) {
            sb.append(tableInfo.getHiveColumnName());
            sb.append(" ");
            sb.append(getHiveDataType(tableInfo.getDataType(), tableInfo.getColumnType()));
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);

        sb.append(")");
        sb.append(" STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler' ");
        sb.append(" WITH SERDEPROPERTIES ('hbase.columns.mapping'=':key,");
        //拼接对应字段
        for (TableInfo tableInfo : list) {
            sb.append("cf:");
            sb.append(tableInfo.getHbaseColumnName());
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);

        sb.append("')");
        sb.append(" TBLPROPERTIES('hbase.table.name'='");
        //拼接hbase表名
        sb.append(hbaseTableName);
        sb.append("')");
        return sb.toString();
    }

    private static String getHiveDataType(String dataType,String columnType){
        dataType = dataType==null?"":dataType.trim().toLowerCase();
        switch (dataType) {
            case "tinyint": return "tinyint";
            case "smallint": return "smallint";
            case "mediumint": return "mediumint";
            case "int":
            case "bit": return "int";
            case "bigint": return "bigint";
            case "double": return "double";
            case "float": return "float";

            case "decimal":
            case "char":
            case "varchar": return columnType.trim().toLowerCase();

            case "date":
            case "time":
            case "year":
            case "timestamp":
            case "datetime":
            case "tinyblob":
            case "blob":
            case "mediumblob":
            case "longblob":
            case "tinytext":
            case "text":
            case "mediumtext":
            case "longtext":

            case "enum":
            case "set":
            case "point":
            case "linestring":
            case "polygon":
            case "geometry":
            case "multipoint":
            case "multilinestring":
            case "multipolygon":
            case "geometrycollection": return "string";

            case "binary":
            case "varbinary": return "binary";

            default: return "string";
        }
    }

}
