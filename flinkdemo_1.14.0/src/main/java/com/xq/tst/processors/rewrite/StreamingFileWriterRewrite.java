package com.xq.tst.processors.rewrite;

import com.ksyun.dc.streaming.flink.failure.FailureHandler;
import com.ksyun.dc.streaming.flink.failure.ThrowIfFail;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.state.StateInitializationContext;
import org.apache.flink.runtime.state.StateSnapshotContext;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.filesystem.stream.AbstractStreamingWriter;
import org.apache.flink.table.filesystem.stream.PartitionCommitInfo;
import org.apache.flink.table.filesystem.stream.PartitionCommitPredicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import static org.apache.flink.table.filesystem.FileSystemConnectorOptions.SINK_PARTITION_COMMIT_POLICY_KIND;

/** Writer for emitting {@link PartitionCommitInfo} to downstream. */
public class StreamingFileWriterRewrite<IN> extends AbstractStreamingWriter<IN, PartitionCommitInfo> {

    private static final long serialVersionUID = 2L;

    private final List<String> partitionKeys;
    private final Configuration conf;

    private transient Set<String> currentNewPartitions;
    private transient TreeMap<Long, Set<String>> newPartitions;
    private transient Set<String> committablePartitions;
    private transient Map<String, Long> inProgressPartitions;

    private transient PartitionCommitPredicate partitionCommitPredicate;

    private String failureHandlerProxyEnable;
    private String shutdownIfFail;
    private String shutdownInTimeIfFail;
    private String shutdownWithFailCount;
    private String shutdownCondition;
    private String logIfFail;
    private String logFieldsIfFail;
    private String logPercentIfFail;

    private ThrowIfFail throwIfFail;
    private Throwable throwException = null;

    private Map<Object,Object> configs;
    private RowTypeInfo rowTypeInfo;

    public StreamingFileWriterRewrite(
            long bucketCheckInterval,
            StreamingFileSink.BucketsBuilder<
                    IN, String, ? extends StreamingFileSink.BucketsBuilder<IN, String, ?>>
                    bucketsBuilder,
            List<String> partitionKeys,
            Configuration conf,
            Map<Object,Object> configs,
            RowTypeInfo rowTypeInfo) {
        super(bucketCheckInterval, bucketsBuilder);
        this.partitionKeys = partitionKeys;
        this.conf = conf;
        this.configs = configs;
        this.rowTypeInfo = rowTypeInfo;
    }

    @Override
    public void initializeState(StateInitializationContext context) throws Exception {
        if (isPartitionCommitTriggerEnabled()) {
            partitionCommitPredicate =
                    PartitionCommitPredicate.create(conf, getUserCodeClassloader(), partitionKeys);
        }

        currentNewPartitions = new HashSet<>();
        newPartitions = new TreeMap<>();
        committablePartitions = new HashSet<>();
        inProgressPartitions = new HashMap<>();
        super.initializeState(context);
    }

    @Override
    protected void partitionCreated(String partition) {
        currentNewPartitions.add(partition);
        inProgressPartitions.putIfAbsent(
                partition, getProcessingTimeService().getCurrentProcessingTime());
    }

    @Override
    protected void partitionInactive(String partition) {
        committablePartitions.add(partition);
        inProgressPartitions.remove(partition);
    }

    @Override
    protected void onPartFileOpened(String s, Path newPath) {}

    @Override
    public void snapshotState(StateSnapshotContext context) throws Exception {
        closePartFileForPartitions();
        super.snapshotState(context);
        newPartitions.put(context.getCheckpointId(), new HashSet<>(currentNewPartitions));
        currentNewPartitions.clear();
    }

    private boolean isPartitionCommitTriggerEnabled() {
        // when partition keys and partition commit policy exist,
        // the partition commit trigger is enabled
        return partitionKeys.size() > 0 && conf.contains(SINK_PARTITION_COMMIT_POLICY_KIND);
    }

    @Override
    public void open(){
        failureHandlerProxyEnable = configs.getOrDefault("failureHandlerProxyEnable", "false").toString();
        shutdownIfFail = configs.getOrDefault("shutdownIfFail", "false").toString();
        shutdownInTimeIfFail = configs.getOrDefault("shutdownInTimeIfFail", "1").toString();
        shutdownWithFailCount = configs.getOrDefault("shutdownWithFailCount", "1").toString();
        shutdownCondition = configs.getOrDefault("shutdownCondition", FailureHandler.ONE_FAILURE()).toString();
        throwIfFail = new ThrowIfFail(Integer.valueOf(shutdownWithFailCount), Integer.valueOf(shutdownInTimeIfFail), shutdownCondition);
        logIfFail = configs.getOrDefault("logIfFail", "false").toString();
        logFieldsIfFail = configs.getOrDefault("logFieldsIfFail", "").toString();
        logPercentIfFail = configs.getOrDefault("logPercentIfFail", "100").toString();
    }

    @Override
    public void processElement(StreamRecord<IN> element) throws Exception {

        try {
            super.processElement(element);
            if (Boolean.valueOf(failureHandlerProxyEnable)) {
                // 异常策略、记录出错次数, 到达次数则停止
                FailureHandler.shutdownProxy(Boolean.valueOf(shutdownIfFail), throwIfFail, throwException, "Hive");
            }
        } catch (Exception ioe) {
            LOG.error("buckets.onElement IOException", ioe);
            if (Boolean.valueOf(failureHandlerProxyEnable)) {
                // 异常打印日志
                FailureHandler.logProxy(Boolean.valueOf(logIfFail),
                        Integer.valueOf(logPercentIfFail),
                        FailureHandler.getHiveLog((RowData) element.getValue(), logFieldsIfFail, rowTypeInfo),
                        FailureHandler.LOG_RANDOM());
                // 失败次数计数
                throwIfFail.linkedListAdd();
            } else {
                throw ioe;
            }
        }
    }

    /** Close in-progress part file when partition is committable. */
    private void closePartFileForPartitions() throws Exception {
        if (partitionCommitPredicate != null) {
            final Iterator<Map.Entry<String, Long>> iterator =
                    inProgressPartitions.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Long> entry = iterator.next();
                String partition = entry.getKey();
                Long creationTime = entry.getValue();
                PartitionCommitPredicate.PredicateContext predicateContext =
                        PartitionCommitPredicate.createPredicateContext(
                                partition,
                                creationTime,
                                processingTimeService.getCurrentProcessingTime(),
                                currentWatermark);
                if (partitionCommitPredicate.isPartitionCommittable(predicateContext)) {
                    // if partition is committable, close in-progress part file in this partition
                    buckets.closePartFileForBucket(partition);
                    iterator.remove();
                }
            }
        }
    }

    @Override
    protected void commitUpToCheckpoint(long checkpointId) throws Exception {
        super.commitUpToCheckpoint(checkpointId);

        NavigableMap<Long, Set<String>> headPartitions =
                this.newPartitions.headMap(checkpointId, true);
        Set<String> partitions = new HashSet<>(committablePartitions);
        committablePartitions.clear();
        headPartitions.values().forEach(partitions::addAll);
        headPartitions.clear();

        output.collect(
                new StreamRecord<>(
                        new PartitionCommitInfo(
                                checkpointId,
                                getRuntimeContext().getIndexOfThisSubtask(),
                                getRuntimeContext().getNumberOfParallelSubtasks(),
                                new ArrayList<>(partitions))));
    }
}

