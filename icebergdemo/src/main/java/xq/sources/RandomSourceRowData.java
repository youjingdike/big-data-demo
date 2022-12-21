package com.xq.sources;

import org.apache.flink.api.common.state.CheckpointListener;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.operators.StreamingRuntimeContext;
import org.apache.flink.streaming.runtime.tasks.ProcessingTimeCallback;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.data.TimestampData;
import org.apache.flink.table.data.binary.BinaryStringData;
import org.apache.flink.types.RowKind;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class RandomSourceRowData extends RichSourceFunction<RowData> implements CheckpointedFunction, CheckpointListener, ProcessingTimeCallback {
    private boolean isCancel = false;
    private boolean isPrint = true;
    private long count = 0L;
    private transient ListState<Long> checkpointedCount;

    private String[] dateArr = {"2022-01-01","2022-02-01","2022-03-01"};
    private String[] nameArr = {"a","b","c"};

    public RandomSourceRowData() {
    }

    public RandomSourceRowData(boolean isPrint) {
        this.isPrint = isPrint;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        StreamingRuntimeContext runtimeContext = (StreamingRuntimeContext)getRuntimeContext();
//        runtimeContext.getProcessingTimeService().registerTimer()；
    }

    @Override
    public void run(SourceContext<RowData> sourceContext) throws Exception {
        Random random = new Random();

        Map<Integer, Double> map = new HashMap(10);
        for (int i = 0; i < 10; i++) {
            map.put(i, 60 + random.nextGaussian() * 20);
        }
        while (!isCancel) {
            Iterator<Map.Entry<Integer, Double>> iterator = map.entrySet().iterator();
            for (; iterator.hasNext(); ) {
                Map.Entry<Integer, Double> next = iterator.next();
                map.put(next.getKey(), next.getValue() + random.nextGaussian());
            }
            map.forEach((k, v) -> {
                if (isPrint) {
                    System.out.println(k+":"+v);
                }
//                .column("uuid VARCHAR(20)")
//                        .column("name VARCHAR(10)")
//                        .column("age INT")
//                        .column("ts TIMESTAMP(3)")
//                        .column("`partition` VARCHAR(20)")
                //在做checkpoint时，状态的修改要做到同步，否则状态可能会有问题。
                synchronized (sourceContext.getCheckpointLock()) {
                    GenericRowData genericRowData = new GenericRowData(RowKind.INSERT, 5);
                    genericRowData.setField(0, new BinaryStringData(k.toString()));//uuid
                    genericRowData.setField(1,new BinaryStringData(nameArr[random.nextInt(3)]+random.nextInt(100)));//name
                    genericRowData.setField(2,random.nextInt(80));//age
                    genericRowData.setField(3,TimestampData.fromLocalDateTime(LocalDateTime.now()));//ts
                    genericRowData.setField(4,new BinaryStringData(dateArr[random.nextInt(3)]));//partition
                    if (isPrint) {
                        System.out.println(genericRowData);
                    }
                    sourceContext.collect(genericRowData);
                    count++;
                }
            });
            // 间隔200ms
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
