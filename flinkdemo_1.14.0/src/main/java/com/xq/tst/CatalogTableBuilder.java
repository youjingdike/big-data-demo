package com.xq.tst;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.catalog.*;
import org.apache.flink.table.types.DataType;
import org.apache.flink.util.Preconditions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CatalogTableBuilder {

    private String[] primaryKeys;
    private String[] fieldNames;
    private String[] partitionKeys;
    private DataType[] dataTypes;
    private Map<String, String> props;
    private TypeInformation[] typeIn;

    public CatalogTableBuilder() {
    }

    public CatalogTableBuilder setPrimaryKeys(String[] primaryKeys) {
        this.primaryKeys = primaryKeys;
        return this;
    }

    public CatalogTableBuilder setFieldNames(String[] fieldNames) {
        this.fieldNames = fieldNames;
        return this;
    }

    public CatalogTableBuilder setDataTypes(DataType[] dataTypes) {
        this.dataTypes = dataTypes;
        return this;
    }

    public CatalogTableBuilder setProps(Map<String, String> props) {
        this.props = props;
        return this;
    }

    public CatalogTableBuilder setPartitionKeys(String[] partitionKeys) {
        this.partitionKeys = partitionKeys;
        return this;
    }


    public CatalogTableBuilder setTypeIn(TypeInformation[] typeIn) {
        this.typeIn = typeIn;
        return this;
    }

    public ResolvedCatalogTable builder() {
        return new ResolvedCatalogTable(buildOrigin(), buildSchema());
    }

    private ResolvedSchema buildSchema() {
        if (primaryKeys!=null) {
            return buildResolvedSchema(Arrays.asList(fieldNames), Arrays.asList(dataTypes), Arrays.asList(primaryKeys));
        }
        return buildResolvedSchema(Arrays.asList(fieldNames), Arrays.asList(dataTypes), null);
    }

    private ResolvedSchema buildResolvedSchema(
            List<String> columnNames, List<DataType> columnDataTypes, List<String> primaryKeyNames) {
        Preconditions.checkArgument(columnNames.size() == columnDataTypes.size(),
                "Mismatch between number of columns names and data types.");
        final List<Column> columns =
                IntStream.range(0, columnNames.size())
                        .mapToObj(i -> Column.physical(columnNames.get(i), columnDataTypes.get(i)))
                        .collect(Collectors.toList());
        if (primaryKeyNames != null) {
            return new ResolvedSchema(columns, Collections.emptyList(), UniqueConstraint.primaryKey("primaryKey", primaryKeyNames));
        } else {
            return ResolvedSchema.of(columns);
        }
    }

    private CatalogTable buildOrigin() {

        props.put("sink.partition-commit.trigger", "process-time");
        props.put("sink.partition-commit.delay", "0s");
        props.put("sink.partition-commit.policy.kind", "metastore");
        return new CatalogTableImpl(new TableSchema(fieldNames, typeIn), Arrays.asList(partitionKeys), props, "");
    }


}
