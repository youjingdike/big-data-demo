/*
package org.apache.flink.formats.orc;

import org.apache.flink.api.common.serialization.BulkWriter;
import org.apache.flink.types.Row;
import org.apache.flink.utils.GetFieldUtil;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.hadoop.hive.ql.exec.vector.*;
import org.apache.hadoop.hive.serde2.io.HiveDecimalWritable;
import org.apache.orc.TypeDescription;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OrcBulkWriter<T> implements BulkWriter<T> {
    private org.apache.orc.Writer orcWriter;

    private TypeDescription schema;

//    private TypeDescription assignerColumnSchema;

    private VectorizedRowBatch batch;

    private GetFieldUtil getFieldUtil = new GetFieldUtil<T>();

    private Collection assignerColumnNumber;

    public OrcBulkWriter(org.apache.orc.Writer orcWriter, TypeDescription schema, TypeDescription assignerColumnSchema, Collection assignerColumnNumber) {
        this.orcWriter = orcWriter;
        this.schema = schema;
//        this.assignerColumnSchema = assignerColumnSchema;
        this.batch = assignerColumnSchema.createRowBatch();
        this.assignerColumnNumber = assignerColumnNumber;
    }

    @Override
    public void addElement(T element) throws IOException {
        int index = this.batch.size++;
        List<TypeDescription> fieldTypes = schema.getChildren();
        //fill up the orc value
        int flip = 0;
        for (int i = 0; i < fieldTypes.size(); i++) {
            if (assignerColumnNumber.contains(i)) {
                flip++;
                continue;
            }
            ColumnVector cv = batch.cols[i - flip];
            Object colVal = getFieldUtil.getFieldByColumnNumber(element, i);

            fillColumnVector(cv, colVal, fieldTypes.get(i), index);
        }

        if (batch.size == VectorizedRowBatch.DEFAULT_SIZE) {
            flush();
        }
    }

    private void fillColumnVector(ColumnVector cv, Object colVal, TypeDescription orcType, int index) {
        if (colVal != null) {
            fillNoNullColumnVector(cv, colVal, orcType, index);
        } else {
            cv.noNulls = false;
            cv.isNull[index] = true;
        }
    }

    private void fillNoNullColumnVector(ColumnVector cv, Object colVal, TypeDescription orcType, int index) {
        switch (orcType.getCategory()) {
            case BOOLEAN:
            case BYTE:
            case SHORT:
            case INT:
            case LONG:
                LongColumnVector longCv = (LongColumnVector) cv;
                longCv.vector[index] = Long.parseLong(colVal.toString());
                break;
            case DATE:
                LongColumnVector dateCv = (LongColumnVector) cv;
                dateCv.vector[index] = LocalDate.parse(colVal.toString()).toEpochDay();
                break;
            case FLOAT:
            case DOUBLE:
                DoubleColumnVector doubleCv = (DoubleColumnVector) cv;
                doubleCv.vector[index] = Double.parseDouble(colVal.toString());
                break;
            case CHAR:
            case VARCHAR:
            case STRING:
                BytesColumnVector bytesCv = (BytesColumnVector) cv;
                bytesCv.setVal(index, colVal.toString().getBytes(Charset.forName("utf-8")));
                break;
            case TIMESTAMP:
                TimestampColumnVector timestampCv = (TimestampColumnVector) cv;
                Timestamp timestamp = (Timestamp) colVal;
                timestampCv.time[index] = timestamp.getTime();
                timestampCv.nanos[index] = timestamp.getNanos();
                break;
            case DECIMAL:
                DecimalColumnVector decimalCv = (DecimalColumnVector) cv;
                BigDecimal bigDecimal = (BigDecimal) colVal;
                HiveDecimal hiveDecimal = HiveDecimal.create(bigDecimal);
                HiveDecimalWritable hiveDecimalWritable = new HiveDecimalWritable(hiveDecimal);
                decimalCv.precision = Short.valueOf(bigDecimal.precision() + "");
                decimalCv.scale = Short.valueOf(bigDecimal.scale() + "");
                decimalCv.vector[index] = hiveDecimalWritable;
                break;
            case MAP:
                fillNoNullMapColumnVector(cv, colVal, orcType, index);
                break;
            case LIST:
                fillNoNullListColumnVector(cv, colVal, orcType, index);
                break;
            case STRUCT:
                fillNoNullStructColumnVector(cv, colVal, orcType, index);
                break;
            case UNION:
                throw new UnsupportedOperationException("UNION type not supported yet");
            default:
                throw new IllegalArgumentException("Unknown type " + orcType);
        }
    }

    private void fillNoNullStructColumnVector(ColumnVector cv, Object colVal, TypeDescription orcType, int index) {
        StructColumnVector structCv = (StructColumnVector) cv;

        List<TypeDescription> childrenTypes = orcType.getChildren();
        Row row = (Row) colVal;
        for (int i = 0; i < childrenTypes.size(); i++) {
            fillColumnVector(structCv.fields[i], row.getField(i), childrenTypes.get(i), i);
        }
    }

    private void fillNoNullListColumnVector(ColumnVector cv, Object colVal, TypeDescription orcType, int index) {
        ListColumnVector listCv = (ListColumnVector) cv;

        TypeDescription fieldType = orcType.getChildren().get(0);
        Object[] list = (Object[]) colVal;

        listCv.lengths[index] = list.length;
        listCv.offsets[index] = listCv.childCount;
        listCv.childCount += listCv.lengths[index];
        listCv.child.ensureSize(listCv.childCount, true);

        for (int i = 0; i < list.length; i++) {
            fillColumnVector(listCv.child, list[i], fieldType, i);
        }
    }

    private void fillNoNullMapColumnVector(ColumnVector cv, Object colVal, TypeDescription orcType, int index) {
        MapColumnVector mapCv = (MapColumnVector) cv;
        ColumnVector keys = mapCv.keys;
        ColumnVector values = mapCv.values;
        List<TypeDescription> fieldType = orcType.getChildren();
        TypeDescription keyType = fieldType.get(0);
        TypeDescription valueType = fieldType.get(1);

        Map map = (Map) colVal;
        Iterator<Map.Entry> it = map.entrySet().iterator();

        mapCv.offsets[index] = mapCv.childCount;
        mapCv.lengths[index] = map.size();
        mapCv.childCount += map.size();

        int i = 0;
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            fillColumnVector(keys, entry.getKey(), keyType, i++);
            fillColumnVector(values, entry.getValue(), valueType, i++);
        }
    }

    @Override
    public void flush() throws IOException {
        if (batch.size != 0) {
            orcWriter.addRowBatch(batch);
            batch.reset();
        }
    }

    @Override
    public void finish() throws IOException {
        if (batch.size != 0) {
            orcWriter.addRowBatch(batch);
            batch.reset();
        }
        orcWriter.close();
    }
}
*/
