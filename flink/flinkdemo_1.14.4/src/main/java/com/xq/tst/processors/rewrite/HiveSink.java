package com.xq.tst.processors.rewrite;

import org.apache.flink.api.common.serialization.BulkWriter;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.connectors.hive.FlinkHiveException;
import org.apache.flink.connectors.hive.HadoopFileSystemFactory;
import org.apache.flink.connectors.hive.HiveOptions;
import org.apache.flink.connectors.hive.HiveRowDataPartitionComputer;
import org.apache.flink.connectors.hive.HiveTableMetaStoreFactory;
import org.apache.flink.connectors.hive.read.HiveCompactReaderFactory;
import org.apache.flink.connectors.hive.util.HiveConfUtils;
import org.apache.flink.connectors.hive.util.JobConfUtils;
import org.apache.flink.connectors.hive.write.HiveBulkWriterFactory;
import org.apache.flink.connectors.hive.write.HiveWriterFactory;
import org.apache.flink.hive.shaded.formats.parquet.row.ParquetRowDataBuilder;
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
import org.apache.flink.table.catalog.hive.util.HiveReflectionUtils;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.filesystem.FileSystemConnectorOptions;
import org.apache.flink.table.filesystem.FileSystemTableSink;
import org.apache.flink.table.filesystem.stream.PartitionCommitInfo;
import org.apache.flink.table.filesystem.stream.compact.CompactReader;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.RowType;
import org.apache.flink.util.Preconditions;
import org.apache.flink.util.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import static org.apache.flink.table.catalog.hive.util.HiveTableUtil.checkAcidTable;
import static org.apache.flink.table.filesystem.FileSystemConnectorOptions.SINK_ROLLING_POLICY_CHECK_INTERVAL;
import static org.apache.flink.table.filesystem.FileSystemConnectorOptions.SINK_ROLLING_POLICY_FILE_SIZE;
import static org.apache.flink.table.filesystem.FileSystemConnectorOptions.SINK_ROLLING_POLICY_ROLLOVER_INTERVAL;
import static org.apache.flink.table.filesystem.stream.compact.CompactOperator.convertToUncompacted;

public class HiveSink {

    private static final Logger LOG = LoggerFactory.getLogger(HiveSink.class);

    private final ReadableConfig flinkConf;
    private final JobConf jobConf;
    private final CatalogTable catalogTable;
    private final ObjectIdentifier identifier;
    private final TableSchema tableSchema;
    private final HiveShim hiveShim;

    private LinkedHashMap<String, String> staticPartitionSpec = new LinkedHashMap<>();

    private final Integer configuredParallelism;
    private final Map<Object, Object> configs;
    private final RowTypeInfo rowTypeInfo;

    public HiveSink(
            ReadableConfig flinkConf,
            JobConf jobConf,
            ObjectIdentifier identifier,
            CatalogTable table,
            int configuredParallelism,
            Map<Object, Object> configs,
            RowTypeInfo rowTypeInfo) {
        this.flinkConf = flinkConf;
        this.jobConf = jobConf;
        this.identifier = identifier;
        this.catalogTable = table;
        this.hiveShim = HiveShimLoader.loadHiveShim(jobConf.get("hive.version"));
        this.tableSchema = (table.getSchema());
        this.configuredParallelism = configuredParallelism;
        this.configs = configs;
        this.rowTypeInfo = rowTypeInfo;
    }

    public DataStreamSink<?> consume(DataStream<RowData> dataStream) throws Exception{
        checkAcidTable(catalogTable.getOptions(), identifier.toObjectPath());

        try (HiveMetastoreClientWrapper client =
                     HiveMetastoreClientFactory.create(HiveConfUtils.create(jobConf), jobConf.get("hive.version"))) {
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

            Properties tableProps = HiveReflectionUtils.getTableMetadata(hiveShim, table);
            return createStreamSink(dataStream, sd, tableProps, writerFactory, fileNamingBuilder,
                    parallelism, configs, rowTypeInfo);
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
            final int parallelism,
            Map<Object, Object> configs,
            RowTypeInfo rowTypeInfo) throws Exception{
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
                            parallelism,
                            configs,
                            rowTypeInfo);
        } else {
            writerStream =
                    StreamingSink.writer(
                            dataStream,
                            bucketCheckInterval,
                            builder,
                            parallelism,
                            getPartitionKeys(),
                            conf,
                            configs,
                            rowTypeInfo);
        }

        return StreamingSink.sink(
                writerStream, path, identifier, getPartitionKeys(), msFactory(), fsFactory(), conf);
    }

    private CompactReader.Factory<RowData> createCompactReaderFactory(
            StorageDescriptor sd, Properties properties) {
        return new HiveCompactReaderFactory(
                sd,
                properties,
                jobConf,
                catalogTable,
                jobConf.get("hive.version"),
                (RowType) tableSchema.toRowDataType().getLogicalType(),
                flinkConf.get(HiveOptions.TABLE_EXEC_HIVE_FALLBACK_MAPRED_READER));
    }

    private HiveTableMetaStoreFactory msFactory() throws Exception {
        Class clazz = Class.forName("org.apache.flink.connectors.hive.HiveTableMetaStoreFactory");
        Constructor constructor = clazz.getDeclaredConstructor(JobConf.class,String.class,String.class,String.class);
        constructor.setAccessible(true);
        return (HiveTableMetaStoreFactory) constructor.newInstance(
                jobConf, jobConf.get("hive.version"), identifier.getDatabaseName(), identifier.getObjectName());
    }

    private HadoopFileSystemFactory fsFactory() throws Exception{

        Class clazz = Class.forName("org.apache.flink.connectors.hive.HadoopFileSystemFactory");
        Constructor constructor = clazz.getDeclaredConstructor(JobConf.class);
        constructor.setAccessible(true);
        return (HadoopFileSystemFactory) constructor.newInstance(jobConf);
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
        List<String> partition = new ArrayList<>();
        for (String partitionColumn : partitionColumns) {
            partition.add(partitionColumn);
        }
        String[] formatNames = new String[formatFieldCount];
        LogicalType[] formatTypes = new LogicalType[formatFieldCount];
        for (int i = 0, j = 0; i < tableSchema.getFieldCount(); i++) {
            if (!partition.contains(tableSchema.getFieldName(i).get())) {
                formatNames[j] = tableSchema.getFieldName(i).get();
                formatTypes[j] = tableSchema.getFieldDataType(i).get().getLogicalType();
                j++;
            }
        }
        RowType formatType = RowType.of(formatTypes, formatNames);
        if (serLib.contains("parquet")) {
            Configuration formatConf = new Configuration(jobConf);
            sd.getSerdeInfo().getParameters().forEach(formatConf::set);
            return Optional.of(
                    ParquetRowDataBuilder.createWriterFactory(
                            formatType, formatConf, jobConf.get("hive.version").startsWith("3.")));
        } else if (serLib.contains("orc")) {
            Configuration formatConf = new ThreadLocalClassLoaderConfiguration(jobConf);
            sd.getSerdeInfo().getParameters().forEach(formatConf::set);
            TypeDescription typeDescription = OrcSplitReaderUtilRewrite.logicalTypeToOrcType(formatType);
            return Optional.of(
                    hiveShim.createOrcBulkWriterFactory(
                            formatConf, typeDescription.toString(), formatTypes));
        } else {
            return Optional.empty();
        }
    }

    // get a staging dir associated with a final dir
    private String toStagingDir(String finalDir, Configuration conf) throws IOException {
        String res = finalDir;
        if (!finalDir.endsWith(Path.SEPARATOR)) {
            res += Path.SEPARATOR;
        }
        // TODO: may append something more meaningful than a timestamp, like query ID
        res += ".staging_" + System.currentTimeMillis();
        Path path = new Path(res);
        FileSystem fs = path.getFileSystem(conf);
        Preconditions.checkState(
                fs.exists(path) || fs.mkdirs(path), "Failed to create staging dir " + path);
        fs.deleteOnExit(path);
        return res;
    }

    private List<String> getPartitionKeys() {
        return catalogTable.getPartitionKeys();
    }

    private String[] getPartitionKeyArray() {
        return getPartitionKeys().toArray(new String[0]);
    }


    public void applyStaticPartition(Map<String, String> partition) {
        // make it a LinkedHashMap to maintain partition column order
        staticPartitionSpec = new LinkedHashMap<>();
        for (String partitionCol : getPartitionKeys()) {
            if (partition.containsKey(partitionCol)) {
                staticPartitionSpec.put(partitionCol, partition.get(partitionCol));
            }
        }
    }

    /**
     * Getting size of the file is too expensive. See {link HiveBulkWriterFactory#create}. We can't
     * check for every element, which will cause great pressure on DFS. Therefore, in this
     * implementation, only check the file size in {link #shouldRollOnProcessingTime}, which can
     * effectively avoid DFS pressure.
     */
    private static class HiveRollingPolicy extends CheckpointRollingPolicy<RowData, String> {

        private final long rollingFileSize;
        private final long rollingTimeInterval;

        private HiveRollingPolicy(long rollingFileSize, long rollingTimeInterval) {
            Preconditions.checkArgument(rollingFileSize > 0L);
            Preconditions.checkArgument(rollingTimeInterval > 0L);
            this.rollingFileSize = rollingFileSize;
            this.rollingTimeInterval = rollingTimeInterval;
        }


        public boolean shouldRollOnCheckpoint(PartFileInfo<String> partFileState) {
            return true;
        }


        public boolean shouldRollOnEvent(PartFileInfo<String> partFileState, RowData element) {
            return false;
        }


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
