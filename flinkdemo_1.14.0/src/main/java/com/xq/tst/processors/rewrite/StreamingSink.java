package com.xq.tst.processors.rewrite;


import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.functions.sink.DiscardingSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.BucketWriter;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.table.catalog.ObjectIdentifier;
import org.apache.flink.table.filesystem.FileSystemFactory;
import org.apache.flink.table.filesystem.TableMetaStoreFactory;
import org.apache.flink.table.filesystem.stream.PartitionCommitInfo;
import org.apache.flink.table.filesystem.stream.PartitionCommitter;
import org.apache.flink.table.filesystem.stream.compact.CompactBucketWriter;
import org.apache.flink.table.filesystem.stream.compact.CompactCoordinator;
import org.apache.flink.table.filesystem.stream.compact.CompactMessages.CoordinatorInput;
import org.apache.flink.table.filesystem.stream.compact.CompactMessages.CoordinatorOutput;
import org.apache.flink.table.filesystem.stream.compact.CompactOperator;
import org.apache.flink.table.filesystem.stream.compact.CompactReader;
import org.apache.flink.table.filesystem.stream.compact.CompactWriter;
import org.apache.flink.util.function.SupplierWithException;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static org.apache.flink.table.filesystem.FileSystemConnectorOptions.SINK_PARTITION_COMMIT_POLICY_KIND;

/**
 * Helper for creating streaming file sink.
 */
public class StreamingSink {
    private StreamingSink() {
    }

    /**
     * Create a file writer by input stream. This is similar to {@link StreamingFileSink}, in
     * addition, it can emit {@link PartitionCommitInfo} to down stream.
     *
     * @return
     */
    public static <T> DataStream<PartitionCommitInfo> writer(
            DataStream<T> inputStream,
            long bucketCheckInterval,
            StreamingFileSink.BucketsBuilder<
                    T, String, ? extends StreamingFileSink.BucketsBuilder<T, String, ?>>
                    bucketsBuilder,
            int parallelism,
            List<String> partitionKeys,
            Configuration conf, Map<Object, Object> configs,
            RowTypeInfo rowTypeInfo) {
        StreamingFileWriterRewrite<T> fileWriter =
                new StreamingFileWriterRewrite<>(bucketCheckInterval, bucketsBuilder,
                        partitionKeys, conf, configs, rowTypeInfo);
        return inputStream
                .transform(
                        StreamingFileWriterRewrite.class.getSimpleName(),
                        TypeInformation.of(PartitionCommitInfo.class),
                        fileWriter
                )
                .setParallelism(parallelism);
    }

    /**
     * Create a file writer with compaction operators by input stream. In addition, it can emit
     * {@link PartitionCommitInfo} to down stream.
     *
     * @return
     */
    public static <T> DataStream<PartitionCommitInfo> compactionWriter(
            DataStream<T> inputStream,
            long bucketCheckInterval,
            StreamingFileSink.BucketsBuilder<
                    T, String, ? extends StreamingFileSink.BucketsBuilder<T, String, ?>>
                    bucketsBuilder,
            FileSystemFactory fsFactory,
            Path path,
            CompactReader.Factory<T> readFactory,
            long targetFileSize,
            int parallelism,
            Map<Object, Object> configs,
            RowTypeInfo rowTypeInfo) {
        CompactFileWriterRewrite<T> writer = new CompactFileWriterRewrite<>(bucketCheckInterval, bucketsBuilder,
                configs, rowTypeInfo);

        SupplierWithException<FileSystem, IOException> fsSupplier =
                (SupplierWithException<FileSystem, IOException> & Serializable)
                        () -> fsFactory.create(path.toUri());

        CompactCoordinator coordinator = new CompactCoordinator(fsSupplier, targetFileSize);

        DataStream<CoordinatorOutput> coordinatorOp =
                inputStream
                        .transform(
                                "streaming-writer",
                                TypeInformation.of(CoordinatorInput.class),
                                writer

                        )
                        .setParallelism(parallelism)
                        .transform(
                                "compact-coordinator",
                                TypeInformation.of(CoordinatorOutput.class),
                                coordinator
                        )
                        .setParallelism(1)
                        .setMaxParallelism(1);

        CompactWriter.Factory<T> writerFactory =
                CompactBucketWriter.factory(
                        (SupplierWithException<BucketWriter<T, String>, IOException> & Serializable)
                                bucketsBuilder::createBucketWriter);

        CompactOperator<T> compacter =
                new CompactOperator<>(fsSupplier, readFactory, writerFactory);

        return coordinatorOp
                .broadcast()
                .transform(
                        "compact-operator",
                        TypeInformation.of(PartitionCommitInfo.class),
                        compacter
                )
                .setParallelism(parallelism);
    }

    /**
     * Create a sink from file writer. Decide whether to add the node to commit partitions according
     * to options.
     */
    public static DataStreamSink<?> sink(
            DataStream<PartitionCommitInfo> writer,
            Path locationPath,
            ObjectIdentifier identifier,
            List<String> partitionKeys,
            TableMetaStoreFactory msFactory,
            FileSystemFactory fsFactory,
            Configuration options) {
        DataStream<?> stream = writer;
        if (partitionKeys.size() > 0 && options.contains(SINK_PARTITION_COMMIT_POLICY_KIND)) {
            PartitionCommitter committer =
                    new PartitionCommitter(
                            locationPath, identifier, partitionKeys, msFactory, fsFactory, options);
            stream =
                    writer.transform(
                                    PartitionCommitter.class.getSimpleName(), Types.VOID, committer)
                            .setParallelism(1)
                            .setMaxParallelism(1);
        }

        return stream.addSink(new DiscardingSink<>()).name("end").setParallelism(1);
    }
}
