package org.apache.flink.formats.orc;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.typeinfo.*;
import org.apache.flink.api.java.typeutils.MapTypeInfo;
import org.apache.flink.api.java.typeutils.ObjectArrayTypeInfo;
import org.apache.flink.api.java.typeutils.RowTypeInfo;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class OrcSchema implements Serializable {
    private static Map<Object, String> orcFlinkTypeMapper;
    private final String[] fieldNames;
    private final TypeInformation[] flinkFieldTypes;
    private final JSONObject schemaJsonObject;

    static {
        orcFlinkTypeMapper = new HashMap<>();
        orcFlinkTypeMapper.put(Types.BYTE, OrcType.TYPE_TINYINT);
        orcFlinkTypeMapper.put(Types.STRING, OrcType.TYPE_STRING);
        orcFlinkTypeMapper.put(Types.BOOLEAN, OrcType.TYPE_BOOLEAN);
        orcFlinkTypeMapper.put(Types.SHORT, OrcType.TYPE_SMALLINT);
        orcFlinkTypeMapper.put(Types.INT, OrcType.TYPE_INT);
        orcFlinkTypeMapper.put(Types.LONG, OrcType.TYPE_BIGINT);
        orcFlinkTypeMapper.put(Types.FLOAT, OrcType.TYPE_FLOAT);
        orcFlinkTypeMapper.put(Types.DOUBLE, OrcType.TYPE_DOUBLE);
        orcFlinkTypeMapper.put(Types.BIG_DEC, OrcType.TYPE_DECIMAL);
        orcFlinkTypeMapper.put(Types.SQL_TIMESTAMP, OrcType.TYPE_TIMESTAMP);
        orcFlinkTypeMapper.put(Types.SQL_DATE, OrcType.TYPE_DATE);
        orcFlinkTypeMapper.put(Types.SQL_TIME, OrcType.TYPE_TIME);
    }

    public OrcSchema(String[] fieldNames, TypeInformation[] flinkFieldTypes, JSONObject schemaJsonObject) {
        this.fieldNames = fieldNames;
        this.flinkFieldTypes = flinkFieldTypes;
        this.schemaJsonObject = schemaJsonObject;
    }

    public static String getOrcFieldTypeSchema(TypeInformation typeInfo) {
        if (typeInfo instanceof BasicTypeInfo || typeInfo instanceof SqlTimeTypeInfo) {
            return orcFlinkTypeMapper.get(typeInfo);
        } else if (typeInfo instanceof MapTypeInfo) {
            MapTypeInfo mapTypeInfo = (MapTypeInfo) typeInfo;
            return (new OrcMapType(mapTypeInfo.getKeyTypeInfo(), mapTypeInfo.getValueTypeInfo())).toString();
        } else if (typeInfo instanceof RowTypeInfo) {
            RowTypeInfo rowTypeInfo = (RowTypeInfo) typeInfo;
            rowTypeInfo.getFieldNames();
            rowTypeInfo.getFieldTypes();
            return (new OrcStuctType(rowTypeInfo.getFieldNames(), rowTypeInfo.getFieldTypes())).toString();
        } else if (typeInfo instanceof BasicArrayTypeInfo) {
            BasicArrayTypeInfo basicArrayType = (BasicArrayTypeInfo) typeInfo;
            return (new OrcListType(basicArrayType.getComponentInfo())).toString();
        } else if (typeInfo instanceof ObjectArrayTypeInfo) {
            ObjectArrayTypeInfo objectArrayType = (ObjectArrayTypeInfo) typeInfo;
            return (new OrcListType(objectArrayType.getComponentInfo())).toString();
        } else {
            throw new UnsupportedOperationException("Unsupported This TypeInfo " + typeInfo.toString());
        }
    }

    public int getFieldCount() {
        return this.fieldNames.length;
    }

    public String getFieldLowerName(int index) {
        return this.fieldNames[index].toLowerCase();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("struct<");
        for (int i = 0; i < this.getFieldCount(); i++) {
            fillSchemaWithFieldType(sb, i, schemaJsonObject);
        }
        return sb.substring(0, sb.length() - 1) + ">";
    }

    /**
     * 生成 orc schema，跳过分区字段
     *
     * @param assignerColumnNumber
     * @return
     */
    public String skipAssignerColumn(Collection assignerColumnNumber) {
        StringBuffer sb = new StringBuffer();
        sb.append("struct<");
        for (int i = 0; i < this.getFieldCount(); i++) {
            if (assignerColumnNumber.contains(i)) {
                continue;
            }

            fillSchemaWithFieldType(sb, i, schemaJsonObject);
        }
        return sb.substring(0, sb.length() - 1) + ">";
    }

    /**
     * 用字段数据类型填充 orc schema
     *
     * @param sb               字符串
     * @param index            字段数组索引
     * @param schemaJsonObject 带有长度的数据管理schema
     */
    private void fillSchemaWithFieldType(StringBuffer sb, Integer index, JSONObject schemaJsonObject) {

        String length = "";
        try {
            length = schemaJsonObject.getJSONObject("properties").getJSONObject(fieldNames[index]).getString("length");
        } catch (Exception e) {
            throw new RuntimeException("hive sink field length 解析出错。 请检查数据管理API，以及字段定义长度。" + schemaJsonObject, e);
        }

        if (flinkFieldTypes[index].toString().equals(Types.BIG_DEC.toString())) {
            sb.append(this.getFieldLowerName(index) + ":" + getOrcFieldTypeSchema(flinkFieldTypes[index]) + "(" + length + "),");

        } else if (flinkFieldTypes[index].toString().equals(Types.LONG.toString())) {

            if (Integer.valueOf(length) <= 2) {
                sb.append(this.getFieldLowerName(index) + ":" + OrcType.TYPE_TINYINT + ",");
            } else if (Integer.valueOf(length) <= 4) {
                sb.append(this.getFieldLowerName(index) + ":" + OrcType.TYPE_SMALLINT + ",");
            } else if (Integer.valueOf(length) <= 9) {
                sb.append(this.getFieldLowerName(index) + ":" + OrcType.TYPE_INT + ",");
            } else {
                sb.append(this.getFieldLowerName(index) + ":" + OrcType.TYPE_BIGINT + ",");
            }
        } else {
            sb.append(this.getFieldLowerName(index) + ":" + getOrcFieldTypeSchema(flinkFieldTypes[index]) + ",");
        }

    }

    /**
     * orc type constants
     */
    static final class OrcType {
        public static final String TYPE_STRING = "string";
        public static final String TYPE_BOOLEAN = "boolean";
        public static final String TYPE_TINYINT = "tinyint";
        public static final String TYPE_SMALLINT = "smallint";
        public static final String TYPE_INT = "int";
        public static final String TYPE_BIGINT = "bigint";
        public static final String TYPE_FLOAT = "float";
        public static final String TYPE_DOUBLE = "double";
        public static final String TYPE_DECIMAL = "decimal";
        public static final String TYPE_TIMESTAMP = "timestamp";
        public static final String TYPE_DATE = "date";
        public static final String TYPE_TIME = "time";
    }

    static final class OrcStuctType {
        private String[] allStructFieldNames;
        private TypeInformation[] allStructFieldTypeInfos;

        public OrcStuctType(String[] allStructFieldNames, TypeInformation[] allStructFieldTypeInfos) {
            this.allStructFieldNames = allStructFieldNames;
            this.allStructFieldTypeInfos = allStructFieldTypeInfos;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("struct<");

            for (int i = 0; i < this.allStructFieldNames.length; ++i) {
                if (i > 0) {
                    sb.append(",");
                }

                sb.append(this.allStructFieldNames[i]);
                sb.append(":");
                sb.append(OrcSchema.getOrcFieldTypeSchema(this.allStructFieldTypeInfos[i]));
            }

            sb.append(">");
            return sb.toString();
        }
    }

    static final class OrcMapType {
        private TypeInformation mapKeyTypeInfo;
        private TypeInformation mapValueTypeInfo;

        public OrcMapType(TypeInformation mapKeyTypeInfo, TypeInformation mapValueTypeInfo) {
            this.mapKeyTypeInfo = mapKeyTypeInfo;
            this.mapValueTypeInfo = mapValueTypeInfo;
        }

        @Override
        public String toString() {
            return "map<" + OrcSchema.getOrcFieldTypeSchema(this.mapKeyTypeInfo) + "," + OrcSchema.getOrcFieldTypeSchema(this.mapValueTypeInfo) + ">";
        }
    }

    static final class OrcListType {
        private TypeInformation listElementTypeInfo;

        public OrcListType(TypeInformation listElementTypeInfo) {
            this.listElementTypeInfo = listElementTypeInfo;
        }

        @Override
        public String toString() {
            return "array<" + OrcSchema.getOrcFieldTypeSchema(this.listElementTypeInfo) + ">";
        }

    }
}

