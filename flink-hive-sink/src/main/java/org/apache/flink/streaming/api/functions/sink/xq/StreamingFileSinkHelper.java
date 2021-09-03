/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.streaming.api.functions.sink.xq;

import org.apache.flink.annotation.Internal;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.OperatorStateStore;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.operators.StreamOperator;
import org.apache.flink.streaming.runtime.tasks.ProcessingTimeCallback;
import org.apache.flink.streaming.runtime.tasks.ProcessingTimeService;
import org.apache.flink.types.Row;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Helper for {@link StreamingFileMultiSink}. This helper can be used by {@link RichSinkFunction} or
 * {@link StreamOperator}.
 */
@Internal
public class StreamingFileSinkHelper<IN> implements ProcessingTimeCallback {

    // -------------------------- state descriptors ---------------------------

    private static final ListStateDescriptor<BucketStateInfo> BUCKET_STATE_DESC =
//            new ListStateDescriptor<>("bucket-states", BytePrimitiveArraySerializer.INSTANCE);
            new ListStateDescriptor<>("bucket-states", TypeInformation.of(new TypeHint<BucketStateInfo>() {}));

    private static final ListStateDescriptor<MaxPartCounterStateInfo> MAX_PART_COUNTER_STATE_DESC =
//            new ListStateDescriptor<>("max-part-counter", LongSerializer.INSTANCE);
            new ListStateDescriptor<>("max-part-counter", TypeInformation.of(new TypeHint<MaxPartCounterStateInfo>() {}));

    // --------------------------- fields -----------------------------

    private final long bucketCheckInterval;

    private final ProcessingTimeService procTimeService;

//    private final Buckets<IN, ?> buckets;
    private final Map<String,Buckets<IN, ?>> bucketsMap;

//    private final ListState<byte[]> bucketStates;
    private final ListState<BucketStateInfo> bucketStates;

//    private final ListState<Long> maxPartCountersState;
    private final ListState<MaxPartCounterStateInfo> maxPartCountersState;

    public StreamingFileSinkHelper(
            Map<String,Buckets<IN, ?>> bucketsMap,
            boolean isRestored,
            OperatorStateStore stateStore,
            ProcessingTimeService procTimeService,
            long bucketCheckInterval)
            throws Exception {
        this.bucketCheckInterval = bucketCheckInterval;
        this.bucketsMap = bucketsMap;
            this.bucketStates = stateStore.getListState(BUCKET_STATE_DESC);
        this.maxPartCountersState = stateStore.getUnionListState(MAX_PART_COUNTER_STATE_DESC);
        this.procTimeService = procTimeService;

        if (isRestored) {
            Iterator<BucketStateInfo> it = bucketStates.get().iterator();
            Map<String,BucketStateInfo> bucketStateInfoMap = new HashMap<>();
            while (it.hasNext()) {
                BucketStateInfo bucketStateInfo = it.next();
                //在多并行度的情况下，同一个table的可能拿到多个BucketStateInfo，此处要进行合并
                BucketStateInfo bucketStateInfoExist = bucketStateInfoMap.get(bucketStateInfo.getTableName());
                if (bucketStateInfoExist == null) {
                    bucketStateInfoMap.put(bucketStateInfo.getTableName(), bucketStateInfo);
                } else {
                    List<BucketState> stateListExist = bucketStateInfoExist.getStateList();
                    stateListExist.addAll(bucketStateInfo.getStateList());
                    bucketStateInfoMap.put(bucketStateInfo.getTableName(), bucketStateInfoExist);
                }
            }

            Iterator<MaxPartCounterStateInfo> countIt = maxPartCountersState.get().iterator();
            Map<String, Long> countMap = new HashMap<>();
            while (countIt.hasNext()) {
                MaxPartCounterStateInfo maxPartCounterStateInfo = countIt.next();
                //在多并行度的情况下，同一个table的可能拿到多个MaxPartCounterStateInfo,此处要选择一个最大值
                Long maxPartCounter = countMap.get(maxPartCounterStateInfo.getTableName());
                if (maxPartCounter == null) {
                    countMap.put(maxPartCounterStateInfo.getTableName(), maxPartCounterStateInfo.getMaxPartCounter());
                } else {
                    maxPartCounter = Math.max(maxPartCounterStateInfo.getMaxPartCounter(), maxPartCounter);
                    countMap.put(maxPartCounterStateInfo.getTableName(), maxPartCounter);
                }
            }

            for (Map.Entry<String, Buckets<IN, ?>> entry : bucketsMap.entrySet()) {
                Buckets<IN, ?> buckets = entry.getValue();
                String tableName = entry.getKey();
                buckets.initializeState(bucketStateInfoMap.get(tableName).getStateList(), countMap.get(tableName));
            }
        }

        long currentProcessingTime = procTimeService.getCurrentProcessingTime();
        procTimeService.registerTimer(currentProcessingTime + bucketCheckInterval, this);
    }

    public void commitUpToCheckpoint(long checkpointId) throws Exception {
        for (Map.Entry<String, Buckets<IN, ?>> entry : bucketsMap.entrySet()) {
            Buckets<IN, ?> buckets = entry.getValue();
            buckets.commitUpToCheckpoint(checkpointId);
        }
    }

    public void snapshotState(long checkpointId) throws Exception {
        List<BucketStateInfo> bucketStateInfoList = new ArrayList<>();
        List<MaxPartCounterStateInfo> maxPartCounterStateInfoList = new ArrayList<>();
        for (Map.Entry<String, Buckets<IN, ?>> entry : bucketsMap.entrySet()) {
            Buckets<IN, ?> buckets = entry.getValue();
            buckets.snapshotState(checkpointId, bucketStateInfoList, maxPartCounterStateInfoList);
        }

        bucketStates.clear();
        maxPartCountersState.clear();
        /*把收集到的状态信息保存到state里面*/
        bucketStates.addAll(bucketStateInfoList);
        maxPartCountersState.addAll(maxPartCounterStateInfoList);
    }

    @Override
    public void onProcessingTime(long timestamp) throws Exception {
        final long currentTime = procTimeService.getCurrentProcessingTime();
        for (Map.Entry<String, Buckets<IN, ?>> entry : bucketsMap.entrySet()) {
            Buckets<IN, ?> buckets = entry.getValue();
            buckets.onProcessingTime(currentTime);
        }
        procTimeService.registerTimer(currentTime + bucketCheckInterval, this);
    }

    public void onElement(
            IN value,
            long currentProcessingTime,
            @Nullable Long elementTimestamp,
            long currentWatermark)
            throws Exception {
        if (value instanceof Row) {
            Map rowInfo = (HashMap<String,Object>)(((Row) value).getField(0));
            String tableName = (String) rowInfo.get("tableName");
            bucketsMap.get(tableName).onElement(value, currentProcessingTime, elementTimestamp, currentWatermark);
//            buckets.onElement(value, currentProcessingTime, elementTimestamp, currentWatermark);
        }
    }

    public void close() {
        for (Map.Entry<String, Buckets<IN, ?>> entry : bucketsMap.entrySet()) {
            Buckets<IN, ?> buckets = entry.getValue();
            buckets.close();
        }
    }
}
