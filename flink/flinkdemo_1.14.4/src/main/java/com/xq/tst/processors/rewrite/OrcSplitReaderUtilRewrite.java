package com.xq.tst.processors.rewrite;

import org.apache.flink.core.fs.Path;
import org.apache.flink.orc.OrcColumnarRowSplitReader;
import org.apache.flink.orc.OrcColumnarRowSplitReader.ColumnBatchGenerator;
import org.apache.flink.orc.OrcFilters;
import org.apache.flink.orc.OrcSplitReader;
import org.apache.flink.orc.shim.OrcShim;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.data.vector.ColumnVector;
import org.apache.flink.table.data.vector.VectorizedColumnBatch;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.types.logical.ArrayType;
import org.apache.flink.table.types.logical.DecimalType;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.MapType;
import org.apache.flink.table.types.logical.RowType;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.ql.exec.vector.VectorizedRowBatch;
import org.apache.orc.TypeDescription;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.flink.orc.vector.AbstractOrcColumnVector.createFlinkVector;
import static org.apache.flink.orc.vector.AbstractOrcColumnVector.createFlinkVectorFromConstant;

/**
 * Util for generating {@link OrcSplitReader}.
 */
public class OrcSplitReaderUtilRewrite {

    /**
     * Util for generating partitioned {@link OrcColumnarRowSplitReader}.
     */
    public static OrcColumnarRowSplitReader<VectorizedRowBatch> genPartColumnarRowReader(
            String hiveVersion,
            Configuration conf,
            String[] fullFieldNames,
            DataType[] fullFieldTypes,
            Map<String, Object> partitionSpec,
            int[] selectedFields,
            List<OrcFilters.Predicate> conjunctPredicates,
            int batchSize,
            Path path,
            long splitStart,
            long splitLength)
            throws IOException {

        List<String> nonPartNames = getNonPartNames(fullFieldNames, partitionSpec);

        int[] selectedOrcFields =
                getSelectedOrcFields(fullFieldNames, selectedFields, nonPartNames);

        ColumnBatchGenerator<VectorizedRowBatch> gen =
                (VectorizedRowBatch rowBatch) -> {
                    // create and initialize the row batch
                    ColumnVector[] vectors = new ColumnVector[selectedFields.length];
                    for (int i = 0; i < vectors.length; i++) {
                        String name = fullFieldNames[selectedFields[i]];
                        LogicalType type = fullFieldTypes[selectedFields[i]].getLogicalType();
                        vectors[i] =
                                partitionSpec.containsKey(name)
                                        ? createFlinkVectorFromConstant(
                                        type, partitionSpec.get(name), batchSize)
                                        : createFlinkVector(
                                        rowBatch.cols[nonPartNames.indexOf(name)], type);
                    }
                    return new VectorizedColumnBatch(vectors);
                };

        return new OrcColumnarRowSplitReader<>(
                OrcShim.createShim(hiveVersion),
                conf,
                convertToOrcTypeWithPart(fullFieldNames, fullFieldTypes, partitionSpec.keySet()),
                selectedOrcFields,
                gen,
                conjunctPredicates,
                batchSize,
                path,
                splitStart,
                splitLength);
    }

    public static int[] getSelectedOrcFields(
            String[] fullFieldNames, int[] selectedFields, List<String> nonPartNames) {
        return Arrays.stream(selectedFields)
                .mapToObj(i -> fullFieldNames[i])
                .filter(nonPartNames::contains)
                .mapToInt(nonPartNames::indexOf)
                .toArray();
    }

    public static List<String> getNonPartNames(
            String[] fullFieldNames, Collection<String> partitionKeys) {
        return Arrays.stream(fullFieldNames)
                .filter(n -> !partitionKeys.contains(n))
                .collect(Collectors.toList());
    }

    public static List<String> getNonPartNames(
            String[] fullFieldNames, Map<String, Object> partitionSpec) {
        return Arrays.stream(fullFieldNames)
                .filter(n -> !partitionSpec.containsKey(n))
                .collect(Collectors.toList());
    }

    public static TypeDescription convertToOrcTypeWithPart(
            String[] fullFieldNames, DataType[] fullFieldTypes, Collection<String> partitionKeys) {
        return convertToOrcTypeWithPart(
                fullFieldNames,
                Arrays.stream(fullFieldTypes)
                        .map(DataType::getLogicalType)
                        .toArray(LogicalType[]::new),
                partitionKeys);
    }

    public static TypeDescription convertToOrcTypeWithPart(
            String[] fullFieldNames,
            LogicalType[] fullFieldTypes,
            Collection<String> partitionKeys) {
        List<String> fullNameList = Arrays.asList(fullFieldNames);
        String[] orcNames =
                fullNameList.stream()
                        .filter(n -> !partitionKeys.contains(n))
                        .toArray(String[]::new);
        LogicalType[] orcTypes =
                Arrays.stream(orcNames)
                        .mapToInt(fullNameList::indexOf)
                        .mapToObj(i -> fullFieldTypes[i])
                        .toArray(LogicalType[]::new);
        return logicalTypeToOrcType(RowType.of(orcTypes, orcNames));
    }

    /**
     * See {@code org.apache.flink.table.catalog.hive.util.HiveTypeUtil}.
     */
    public static TypeDescription logicalTypeToOrcType(LogicalType type) {
        type = type.copy(true);
        switch (type.getTypeRoot()) {
            case CHAR:
            case VARCHAR:
                return TypeDescription.createString();
            case BOOLEAN:
                return TypeDescription.createBoolean();
            case VARBINARY:
                if (type.equals(DataTypes.BYTES().getLogicalType())) {
                    return TypeDescription.createBinary();
                } else {
                    throw new UnsupportedOperationException(
                            "Not support other binary type: " + type);
                }
            case DECIMAL:
                DecimalType decimalType = (DecimalType) type;
                return TypeDescription.createDecimal()
                        .withScale(decimalType.getScale())
                        .withPrecision(decimalType.getPrecision());
            case TINYINT:
                return TypeDescription.createByte();
            case SMALLINT:
                return TypeDescription.createShort();
            case INTEGER:
            case BIGINT:
                return TypeDescription.createInt();
            case FLOAT:
                return TypeDescription.createFloat();
            case DOUBLE:
                return TypeDescription.createDouble();
            case DATE:
                return TypeDescription.createDate();
            case TIMESTAMP_WITHOUT_TIME_ZONE:
                return TypeDescription.createTimestamp();
            case ARRAY:
                ArrayType arrayType = (ArrayType) type;
                return TypeDescription.createList(logicalTypeToOrcType(arrayType.getElementType()));
            case MAP:
                MapType mapType = (MapType) type;
                return TypeDescription.createMap(
                        logicalTypeToOrcType(mapType.getKeyType()),
                        logicalTypeToOrcType(mapType.getValueType()));
            case ROW:
                RowType rowType = (RowType) type;
                TypeDescription struct = TypeDescription.createStruct();
                for (int i = 0; i < rowType.getFieldCount(); i++) {
                    struct.addField(
                            rowType.getFieldNames().get(i),
                            logicalTypeToOrcType(rowType.getChildren().get(i)));
                }
                return struct;
            default:
                throw new UnsupportedOperationException("Unsupported type: " + type);
        }
    }
}