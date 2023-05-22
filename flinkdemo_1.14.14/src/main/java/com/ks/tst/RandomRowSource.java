package com.ks.tst;

import org.apache.flink.api.common.state.CheckpointListener;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.runtime.tasks.ProcessingTimeCallback;
import org.apache.flink.types.Row;

import java.util.Random;

public class RandomRowSource extends RichSourceFunction<Row> implements CheckpointedFunction, CheckpointListener, ProcessingTimeCallback {
    private boolean isCancel = false;
    private boolean isPrint = true;
    private long count = 0L;
    private transient ListState<Long> checkpointedCount;

//    private String[] strArr = {"1,a,11,1","2,b,12,2","3,c,13,1","4,d,14,2","5,e,15,1","6,f,16,2","g","h"};
    private String[] strArr = {"a","b","c","d","e","f","g","h"};

    public RandomRowSource() {
    }

    public RandomRowSource(boolean isPrint) {
        this.isPrint = isPrint;
    }

    public RandomRowSource(String[] strArr) {
        if (strArr == null) {
            throw new RuntimeException("strArr is null");
        }
        this.strArr = strArr;
    }

    public RandomRowSource(String[] strArr, boolean isPrint) {
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
    public void run(SourceContext<Row> sourceContext) throws Exception {
        Random random = new Random();
        int i = 0;
        String str = "";
        while (!isCancel) {
            i++;
            str = strArr[random.nextInt(strArr.length)];
            if (isPrint) {
                System.out.println("字符串:"+str);
            }
            //在做checkpoint时，状态的修改要做到同步，否则状态可能会有问题。
            synchronized (sourceContext.getCheckpointLock()) {
                /*Row row = Row.withNames();
                row.setField("id",i+"");
                row.setField("name",str);
                row.setField("age",i+"");
                row.setField("addrId",(random.nextInt(2)+1)+"");
                sourceContext.collect(row);*/
                sourceContext.collect(Row.of(i+"",str,i+"",random.nextInt(2)+1+""));
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
