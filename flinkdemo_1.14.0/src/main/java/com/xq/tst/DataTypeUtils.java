package com.xq.tst;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.table.types.AtomicDataType;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.types.logical.*;
import org.apache.flink.table.types.utils.TypeConversions;


public class DataTypeUtils {

    public  static String TYPE = "type";
    public  static String FORMAT = "format";
    public  static String LENGTH = "length";

    public static DataType[] ex(RowTypeInfo rowTypeInfo) {
        String[] fieldNames = rowTypeInfo.getFieldNames();
        TypeInformation[] rowTypeInfos = new TypeInformation[fieldNames.length];

        for (int i = 0; i < fieldNames.length; i++) {
            rowTypeInfos[i] = rowTypeInfo.getTypeAt(rowTypeInfo.getFieldIndex(fieldNames[i]));
        }

        return TypeConversions.fromLegacyInfoToDataType(rowTypeInfos);
    }

    public static TypeInformation[] en(RowTypeInfo rowTypeInfo) {
        String[] fieldNames = rowTypeInfo.getFieldNames();
        TypeInformation[] rowTypeInfos = new TypeInformation[fieldNames.length];
        for (int i = 0; i < fieldNames.length; i++) {
            rowTypeInfos[i] = rowTypeInfo.getTypeAt(rowTypeInfo.getFieldIndex(fieldNames[i]));
        }

        return rowTypeInfos;
    }

    public static DataType[] generateDataType(RowTypeInfo rowTypeInfo, JSONObject schema,JSONObject notNullSchema) {
        String[] fieldNames = rowTypeInfo.getFieldNames();

        DataType[] dataTypes = new DataType[fieldNames.length];
        for (int i =0;i<fieldNames.length;i++) {
            JSONObject fieldSchema = schema.getJSONObject(fieldNames[i]);
            dataTypes[i] = generateDataType(fieldSchema, !notNullSchema.getBoolean(fieldNames[i]));
        }
        return dataTypes;
    }

    private static DataType generateDataType(JSONObject fieldSchema, boolean fieldNull) {

        String type_case = fieldSchema.getString(TYPE);
        if (fieldSchema.containsKey(FORMAT)) {
            type_case = fieldSchema.getString(FORMAT);
        }
        switch (type_case) {
            case "string":
                int length = fieldSchema.getInteger(LENGTH);
                return new AtomicDataType(new VarCharType(fieldNull, length));
            case "number":
                return new AtomicDataType(new BigIntType(fieldNull));
            case "decimal":
                String[] split = fieldSchema.getString(LENGTH).split(",");
                return new AtomicDataType(new DecimalType(fieldNull, Integer.valueOf(split[0]), Integer.valueOf(split[1])));
            case "date":
                return new AtomicDataType(new DateType(fieldNull));
            case "time":
                return new AtomicDataType(new VarCharType(fieldNull,12));
            case "datetime":
               return new AtomicDataType(new TimestampType(fieldNull, 9));
            default:
                throw new RuntimeException("this type is not exist !");
        }
    }

}
