package com.xq.tst;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.DataType;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RowDataMapper extends RichFlatMapFunction<Row, RowData> {

    private DataFormatConvertersRewrite.RowConverter rowConverter;
    private static final Logger LOG = LoggerFactory.getLogger(RowDataMapper.class);
//    private boolean isStrictCheck;
//
//    private RowTypeInfo rowTypeInfo;
//    private JSONObject schemaObject;
//    private Map<Object, Object> configs;

    public RowDataMapper(DataType[] dataTypes/*, RowTypeInfo rowTypeInfo, Map<Object, Object>  configs*/) {
        this.rowConverter = new DataFormatConvertersRewrite.RowConverter(dataTypes);
//        this.rowTypeInfo = rowTypeInfo;
//        this.configs = configs;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
//        schemaObject = JSON.parseObject(configs.get("schema").toString());
//        isStrictCheck = Boolean.valueOf(configs.getOrDefault("isStrictCheck", "false").toString());
        super.open(parameters);
    }

    @Override
    public void flatMap(Row row, Collector<RowData> collector){

        /*if (isStrictCheck) {
            Boolean isQualified = MetadataCheck.checkFieldLength(rowTypeInfo, schemaObject, row, null);
            if (!isQualified) {
                LOG.error(String.format("数据%s: 字段长度校验不通过！", row));
                return;
            }
        }
        if (!MetadataCheck.checkDecimalLength(rowTypeInfo, schemaObject, row)) {
            LOG.error(String.format("数据%s: 字段长度校验不通过！", row));
            return;
        }*/
        collector.collect(rowConverter.toInternal(row));
    }
}
