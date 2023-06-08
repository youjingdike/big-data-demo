package com.xq.tst;

import org.apache.flink.api.common.state.CheckpointListener;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.runtime.tasks.ProcessingTimeCallback;

import java.util.Random;

public class RandomSource extends RichSourceFunction<String> implements CheckpointedFunction, CheckpointListener, ProcessingTimeCallback {
    private boolean isCancel = false;
    private boolean isPrint = true;
    private long count = 0L;
    private transient ListState<Long> checkpointedCount;

    private String[] strArr = {"a","b","c","d","e","f","g","h"};

    public RandomSource() {
    }

    public RandomSource(boolean isPrint) {
        this.isPrint = isPrint;
    }

    public RandomSource(String[] strArr) {
        if (strArr == null) {
            throw new RuntimeException("strArr is null");
        }
        this.strArr = strArr;
    }

    public RandomSource(String[] strArr,boolean isPrint) {
        this(strArr);
        this.isPrint = isPrint;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
//        StreamingRuntimeContext runtimeContext = (StreamingRuntimeContext)getRuntimeContext();
//        runtimeContext.getProcessingTimeService().registerTimer()；
    }

    @Override
    public void run(SourceContext<String> sourceContext) throws Exception {
        Random random = new Random();
        String str = "";
        while (!isCancel) {
            str = strArr[random.nextInt(strArr.length)];
            if (isPrint) {
                System.out.println("字符串:"+str);
            }
            //在做checkpoint时，状态的修改要做到同步，否则状态可能会有问题。
            synchronized (sourceContext.getCheckpointLock()) {
                sourceContext.collect(str);
                count++;
            }// 间隔200ms
            Thread.sleep(200);
        }
    }

    @Override
    public void cancel() {
        isCancel = true;
    }

    @Override
    public void notifyCheckpointComplete(long checkpointId) throws Exception {

    }

    @Override
    public void snapshotState(FunctionSnapshotContext context) throws Exception {
        this.checkpointedCount.clear();
        this.checkpointedCount.add(count);
    }

    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        this.checkpointedCount = context
                .getOperatorStateStore()
                .getListState(new ListStateDescriptor<>("count", Long.class));
        if (context.isRestored()) {
           for (Long count : this.checkpointedCount.get()) {
              this.count += count;
           }
       }
    }

    @Override
    public void onProcessingTime(long timestamp) throws Exception {

    }
}
