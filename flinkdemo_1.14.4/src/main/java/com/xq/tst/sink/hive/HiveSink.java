package com.xq.tst.sink.hive;

import org.apache.flink.api.common.serialization.BulkWriter;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.connectors.hive.*;
import org.apache.flink.connectors.hive.read.HiveCompactReaderFactory;
import org.apache.flink.connectors.hive.util.HiveConfUtils;
import org.apache.flink.connectors.hive.util.JobConfUtils;
import org.apache.flink.connectors.hive.write.HiveBulkWriterFactory;
import org.apache.flink.connectors.hive.write.HiveWriterFactory;
import org.apache.flink.hive.shaded.formats.parquet.row.ParquetRowDataBuilder;
import org.apache.flink.orc.OrcSplitReaderUtil;
import org.apache.flink.orc.writer.ThreadLocalClassLoaderConfiguration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.HadoopPathBasedBulkFormatBuilder;
import org.apache.flink.streaming.api.functions.sink.filesystem.OutputFileConfig;
import org.apache.flink.streaming.api.functions.sink.filesystem.PartFileInfo;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.CheckpointRollingPolicy;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.catalog.CatalogTable;
import org.apache.flink.table.catalog.ObjectIdentifier;
import org.apache.flink.table.catalog.exceptions.CatalogException;
import org.apache.flink.table.catalog.hive.client.HiveMetastoreClientFactory;
import org.apache.flink.table.catalog.hive.client.HiveMetastoreClientWrapper;
import org.apache.flink.table.catalog.hive.client.HiveShim;
import org.apache.flink.table.catalog.hive.client.HiveShimLoader;
import org.apache.flink.table.catalog.hive.factories.HiveCatalogFactoryOptions;
import org.apache.flink.table.catalog.hive.util.HiveReflectionUtils;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.filesystem.FileSystemConnectorOptions;
import org.apache.flink.table.filesystem.FileSystemTableSink;
import org.apache.flink.table.filesystem.stream.PartitionCommitInfo;
import org.apache.flink.table.filesystem.stream.StreamingSink;
import org.apache.flink.table.filesystem.stream.compact.CompactReader;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.RowType;
import org.apache.flink.table.utils.TableSchemaUtils;
import org.apache.flink.util.Preconditions;
import org.apache.flink.util.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.ql.exec.Utilities;
import org.apache.hadoop.hive.ql.io.HiveOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.orc.TypeDescription;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;

import static org.apache.flink.table.catalog.hive.util.HiveTableUtil.checkAcidTable;
import static org.apache.flink.table.filesystem.FileSystemConnectorOptions.*;
import static org.apache.flink.table.filesystem.FileSystemConnectorOptions.SINK_ROLLING_POLICY_FILE_SIZE;
import static org.apache.flink.table.filesystem.stream.compact.CompactOperator.convertToUncompacted;

public class HiveSink {

    private static final Logger LOG = LoggerFactory.getLogger(HiveSink.class);

    private final ReadableConfig flinkConf;
    private final JobConf jobConf;
    private final CatalogTable catalogTable;
    private final ObjectIdentifier identifier;
    private final TableSchema tableSchema;
    private final String hiveVersion;
    private final HiveShim hiveShim;

    private LinkedHashMap<String, String> staticPartitionSpec = new LinkedHashMap<>();
    private boolean overwrite = false;
    private boolean dynamicGrouping = false;

    @Nullable private final Integer configuredParallelism;


    public HiveSink(
            ReadableConfig flinkConf,
            JobConf jobConf,
            ObjectIdentifier identifier,
            CatalogTable table,
            @Nullable Integer configuredParallelism) {
        this.flinkConf = flinkConf;
        this.jobConf = jobConf;
        this.identifier = identifier;
        this.catalogTable = table;
        hiveVersion =
                Preconditions.checkNotNull(
                        jobConf.get(HiveCatalogFactoryOptions.HIVE_VERSION.key()),
                        "Hive version is not defined");
        hiveShim = HiveShimLoader.loadHiveShim(hiveVersion);
        tableSchema = TableSchemaUtils.getPhysicalSchema(table.getSchema());
        this.configuredParallelism = configuredParallelism;
    }

    public DataStreamSink<?> consume(
            DataStream<RowData> dataStream/*, boolean isBounded, DynamicTableSink.DataStructureConverter converter*/) {
        checkAcidTable(catalogTable.getOptions(), identifier.toObjectPath());

        try (HiveMetastoreClientWrapper client =
                     HiveMetastoreClientFactory.create(HiveConfUtils.create(jobConf), hiveVersion)) {
            Table table = client.getTable(identifier.getDatabaseName(), identifier.getObjectName());
            StorageDescriptor sd = table.getSd();

            Class hiveOutputFormatClz =
                    hiveShim.getHiveOutputFormatClass(Class.forName(sd.getOutputFormat()));
            boolean isCompressed =
                    jobConf.getBoolean(HiveConf.ConfVars.COMPRESSRESULT.varname, false);
            HiveWriterFactory writerFactory =
                    new HiveWriterFactory(
                            jobConf,
                            hiveOutputFormatClz,
                            sd.getSerdeInfo(),
                            tableSchema,
                            getPartitionKeyArray(),
                            HiveReflectionUtils.getTableMetadata(hiveShim, table),
                            hiveShim,
                            isCompressed);
            String extension =
                    Utilities.getFileExtension(
                            jobConf,
                            isCompressed,
                            (HiveOutputFormat<?, ?>) hiveOutputFormatClz.newInstance());

            OutputFileConfig.OutputFileConfigBuilder fileNamingBuilder =
                    OutputFileConfig.builder()
                            .withPartPrefix("part-" + UUID.randomUUID().toString())
                            .withPartSuffix(extension == null ? "" : extension);

            final int parallelism =
                    Optional.ofNullable(configuredParallelism).orElse(dataStream.getParallelism());

            if (overwrite) {
                throw new IllegalStateException("Streaming mode not support overwrite.");
            }

            Properties tableProps = HiveReflectionUtils.getTableMetadata(hiveShim, table);
            return createStreamSink(
                    dataStream, sd, tableProps, writerFactory, fileNamingBuilder, parallelism);
        } catch (TException e) {
            throw new CatalogException("Failed to query Hive metaStore", e);
        } catch (ClassNotFoundException e) {
            throw new FlinkHiveException("Failed to get output format class", e);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new FlinkHiveException("Failed to instantiate output format instance", e);
        }
    }

    private DataStreamSink<?> createStreamSink(
            DataStream<RowData> dataStream,
            StorageDescriptor sd,
            Properties tableProps,
            HiveWriterFactory recordWriterFactory,
            OutputFileConfig.OutputFileConfigBuilder fileNamingBuilder,
            final int parallelism) {
        org.apache.flink.configuration.Configuration conf =
                new org.apache.flink.configuration.Configuration();
        catalogTable.getOptions().forEach(conf::setString);

        String commitPolicies =
                conf.getString(FileSystemConnectorOptions.SINK_PARTITION_COMMIT_POLICY_KIND);
        if (!getPartitionKeys().isEmpty() && StringUtils.isNullOrWhitespaceOnly(commitPolicies)) {
            throw new FlinkHiveException(
                    String.format(
                            "Streaming write to partitioned hive table %s without providing a commit policy. "
                                    + "Make sure to set a proper value for %s",
                            identifier,
                            FileSystemConnectorOptions.SINK_PARTITION_COMMIT_POLICY_KIND.key()));
        }

        HiveRowDataPartitionComputer partComputer =
                new HiveRowDataPartitionComputer(
                        hiveShim,
                        JobConfUtils.getDefaultPartitionName(jobConf),
                        tableSchema.getFieldNames(),
                        tableSchema.getFieldDataTypes(),
                        getPartitionKeyArray());
        FileSystemTableSink.TableBucketAssigner assigner = new FileSystemTableSink.TableBucketAssigner(partComputer);
        HiveRollingPolicy rollingPolicy =
                new HiveRollingPolicy(
                        conf.get(SINK_ROLLING_POLICY_FILE_SIZE).getBytes(),
                        conf.get(SINK_ROLLING_POLICY_ROLLOVER_INTERVAL).toMillis());

        boolean autoCompaction = conf.getBoolean(FileSystemConnectorOptions.AUTO_COMPACTION);
        if (autoCompaction) {
            fileNamingBuilder.withPartPrefix(
                    convertToUncompacted(fileNamingBuilder.build().getPartPrefix()));
        }
        OutputFileConfig outputFileConfig = fileNamingBuilder.build();

        org.apache.flink.core.fs.Path path = new org.apache.flink.core.fs.Path(sd.getLocation());

        StreamingFileSink.BucketsBuilder<RowData, String, ? extends StreamingFileSink.BucketsBuilder<RowData, ?, ?>> builder;
        if (flinkConf.get(HiveOptions.TABLE_EXEC_HIVE_FALLBACK_MAPRED_WRITER)) {
            builder =
                    bucketsBuilderForMRWriter(
                            recordWriterFactory, sd, assigner, rollingPolicy, outputFileConfig);
            LOG.info("Hive streaming sink: Use MapReduce RecordWriter writer.");
        } else {
            Optional<BulkWriter.Factory<RowData>> bulkFactory =
                    createBulkWriterFactory(getPartitionKeyArray(), sd);
            if (bulkFactory.isPresent()) {
                builder =
                        StreamingFileSink.forBulkFormat(
                                        path,
                                        new FileSystemTableSink.ProjectionBulkFactory(
                                                bulkFactory.get(), partComputer))
                                .withBucketAssigner(assigner)
                                .withRollingPolicy(rollingPolicy)
                                .withOutputFileConfig(outputFileConfig);
                LOG.info("Hive streaming sink: Use native parquet&orc writer.");
            } else {
                builder =
                        bucketsBuilderForMRWriter(
                                recordWriterFactory, sd, assigner, rollingPolicy, outputFileConfig);
                LOG.info(
                        "Hive streaming sink: Use MapReduce RecordWriter writer because BulkWriter Factory not available.");
            }
        }

        long bucketCheckInterval = conf.get(SINK_ROLLING_POLICY_CHECK_INTERVAL).toMillis();

        DataStream<PartitionCommitInfo> writerStream;
        if (autoCompaction) {
            long compactionSize =
                    conf.getOptional(FileSystemConnectorOptions.COMPACTION_FILE_SIZE)
                            .orElse(conf.get(SINK_ROLLING_POLICY_FILE_SIZE))
                            .getBytes();

            writerStream =
                    StreamingSink.compactionWriter(
                            dataStream,
                            bucketCheckInterval,
                            builder,
                            fsFactory(),
                            path,
                            createCompactReaderFactory(sd, tableProps),
                            compactionSize,
                            parallelism);
        } else {
            writerStream =
                    StreamingSink.writer(
                            dataStream,
                            bucketCheckInterval,
                            builder,
                            parallelism,
                            getPartitionKeys(),
                            conf);
        }

        return StreamingSink.sink(
                writerStream, path, identifier, getPartitionKeys(), msFactory(), fsFactory(), conf);
    }

    private List<String> getPartitionKeys() {
        return catalogTable.getPartitionKeys();
    }

    private String[] getPartitionKeyArray() {
        return getPartitionKeys().toArray(new String[0]);
    }

    private HiveTableMetaStoreFactory msFactory() {
        return new HiveTableMetaStoreFactory(
                jobConf, hiveVersion, identifier.getDatabaseName(), identifier.getObjectName());
    }

    private HadoopFileSystemFactory fsFactory() {
        return new HadoopFileSystemFactory(jobConf);
    }

    private StreamingFileSink.BucketsBuilder<RowData, String, ? extends StreamingFileSink.BucketsBuilder<RowData, ?, ?>>
    bucketsBuilderForMRWriter(
            HiveWriterFactory recordWriterFactory,
            StorageDescriptor sd,
            FileSystemTableSink.TableBucketAssigner assigner,
            HiveRollingPolicy rollingPolicy,
            OutputFileConfig outputFileConfig) {
        HiveBulkWriterFactory hadoopBulkFactory = new HiveBulkWriterFactory(recordWriterFactory);
        return new HadoopPathBasedBulkFormatBuilder<>(
                new Path(sd.getLocation()), hadoopBulkFactory, jobConf, assigner)
                .withRollingPolicy(rollingPolicy)
                .withOutputFileConfig(outputFileConfig);
    }

    private Optional<BulkWriter.Factory<RowData>> createBulkWriterFactory(
            String[] partitionColumns, StorageDescriptor sd) {
        String serLib = sd.getSerdeInfo().getSerializationLib().toLowerCase();
        int formatFieldCount = tableSchema.getFieldCount() - partitionColumns.length;
        String[] formatNames = new String[formatFieldCount];
        LogicalType[] formatTypes = new LogicalType[formatFieldCount];
        for (int i = 0; i < formatFieldCount; i++) {
            formatNames[i] = tableSchema.getFieldName(i).get();
            formatTypes[i] = tableSchema.getFieldDataType(i).get().getLogicalType();
        }
        RowType formatType = RowType.of(formatTypes, formatNames);
        if (serLib.contains("parquet")) {
            Configuration formatConf = new Configuration(jobConf);
            sd.getSerdeInfo().getParameters().forEach(formatConf::set);
            return Optional.of(
                    ParquetRowDataBuilder.createWriterFactory(
                            formatType, formatConf, hiveVersion.startsWith("3.")));
        } else if (serLib.contains("orc")) {
            Configuration formatConf = new ThreadLocalClassLoaderConfiguration(jobConf);
            sd.getSerdeInfo().getParameters().forEach(formatConf::set);
            TypeDescription typeDescription = OrcSplitReaderUtil.logicalTypeToOrcType(formatType);
            return Optional.of(
                    hiveShim.createOrcBulkWriterFactory(
                            formatConf, typeDescription.toString(), formatTypes));
        } else {
            return Optional.empty();
        }
    }

    private CompactReader.Factory<RowData> createCompactReaderFactory(
            StorageDescriptor sd, Properties properties) {
        return new HiveCompactReaderFactory(
                sd,
                properties,
                jobConf,
                catalogTable,
                hiveVersion,
                (RowType) tableSchema.toRowDataType().getLogicalType(),
                flinkConf.get(HiveOptions.TABLE_EXEC_HIVE_FALLBACK_MAPRED_READER));
    }

    private static class HiveRollingPolicy extends CheckpointRollingPolicy<RowData, String> {

        private final long rollingFileSize;
        private final long rollingTimeInterval;

        private HiveRollingPolicy(long rollingFileSize, long rollingTimeInterval) {
            Preconditions.checkArgument(rollingFileSize > 0L);
            Preconditions.checkArgument(rollingTimeInterval > 0L);
            this.rollingFileSize = rollingFileSize;
            this.rollingTimeInterval = rollingTimeInterval;
        }

        @Override
        public boolean shouldRollOnCheckpoint(PartFileInfo<String> partFileState) {
            return true;
        }

        @Override
        public boolean shouldRollOnEvent(PartFileInfo<String> partFileState, RowData element) {
            return false;
        }

        @Override
        public boolean shouldRollOnProcessingTime(
                PartFileInfo<String> partFileState, long currentTime) {
            try {
                return currentTime - partFileState.getCreationTime() >= rollingTimeInterval
                        || partFileState.getSize() > rollingFileSize;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
