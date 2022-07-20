package com.xq.tst.processors.rewrite;


/*import com.ksyun.dc.streaming.flink.failure.FailureHandler;
import com.ksyun.dc.streaming.flink.failure.ThrowIfFail;*/
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.filesystem.stream.AbstractStreamingWriter;
import org.apache.flink.table.filesystem.stream.compact.CompactMessages;
import org.apache.flink.table.filesystem.stream.compact.CompactMessages.EndCheckpoint;
import org.apache.flink.table.filesystem.stream.compact.CompactMessages.InputFile;

import java.util.Map;

/**
 * Writer for emitting {@link InputFile} and {@link EndCheckpoint} to downstream.
 */
public class CompactFileWriterRewrite<T>
        extends AbstractStreamingWriter<T, CompactMessages.CoordinatorInput> {

    private static final long serialVersionUID = 1L;

    private String failureHandlerProxyEnable;
    private String shutdownIfFail;
    private String shutdownInTimeIfFail;
    private String shutdownWithFailCount;
    private String shutdownCondition;
    private String logIfFail;
    private String logFieldsIfFail;
    private String logPercentIfFail;

//    private ThrowIfFail throwIfFail;
    private Throwable throwException = null;

    private Map<Object,Object> configs;
    private RowTypeInfo rowTypeInfo;

    public CompactFileWriterRewrite(
            long bucketCheckInterval,
            StreamingFileSink.BucketsBuilder<
                    T, String, ? extends StreamingFileSink.BucketsBuilder<T, String, ?>>
                    bucketsBuilder,
            Map<Object,Object> configs,
            RowTypeInfo rowTypeInfo) {
        super(bucketCheckInterval, bucketsBuilder);
        this.configs = configs;
        this.rowTypeInfo = rowTypeInfo;
    }

    @Override
    protected void partitionCreated(String partition) {
    }

    @Override
    protected void partitionInactive(String partition) {
    }

    @Override
    protected void onPartFileOpened(String partition, Path newPath) {
        output.collect(new StreamRecord<>(new InputFile(partition, newPath)));
    }

    @Override
    public void open() throws Exception {
        failureHandlerProxyEnable = configs.getOrDefault("failureHandlerProxyEnable", "false").toString();
        shutdownIfFail = configs.getOrDefault("shutdownIfFail", "false").toString();
        shutdownInTimeIfFail = configs.getOrDefault("shutdownInTimeIfFail", "1").toString();
        shutdownWithFailCount = configs.getOrDefault("shutdownWithFailCount", "1").toString();
//        shutdownCondition = configs.getOrDefault("shutdownCondition", FailureHandler.ONE_FAILURE()).toString();
//        throwIfFail = new ThrowIfFail(Integer.valueOf(shutdownWithFailCount), Integer.valueOf(shutdownInTimeIfFail), shutdownCondition);
        logIfFail = configs.getOrDefault("logIfFail", "false").toString();
        logFieldsIfFail = configs.getOrDefault("logFieldsIfFail", "").toString();
        logPercentIfFail = configs.getOrDefault("logPercentIfFail", "100").toString();
    }

    @Override
    public void processElement(StreamRecord<T> element) throws Exception {
        try {
            super.processElement(element);
            if (Boolean.valueOf(failureHandlerProxyEnable)) {
                // 异常策略、记录出错次数, 到达次数则停止
//                FailureHandler.shutdownProxy(Boolean.valueOf(shutdownIfFail), throwIfFail, throwException, "Hive");
            }
        } catch (Exception ioe) {
            LOG.error("buckets.onElement IOException", ioe);
            if (Boolean.valueOf(failureHandlerProxyEnable)) {
                // 异常打印日志
//                FailureHandler.logProxy(Boolean.valueOf(logIfFail),
//                        Integer.valueOf(logPercentIfFail),
//                        FailureHandler.getHiveLog((RowData) element.getValue(), logFieldsIfFail, rowTypeInfo),
//                        FailureHandler.LOG_RANDOM());
//                // 失败次数计数
//                throwIfFail.linkedListAdd();
            } else {
                throw ioe;
            }
        }

    }

    @Override
    protected void commitUpToCheckpoint(long checkpointId) throws Exception {
        super.commitUpToCheckpoint(checkpointId);
        output.collect(
                new StreamRecord<>(
                        new EndCheckpoint(
                                checkpointId,
                                getRuntimeContext().getIndexOfThisSubtask(),
                                getRuntimeContext().getNumberOfParallelSubtasks())));
    }
}
