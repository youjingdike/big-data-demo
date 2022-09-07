package com.xq.tst.dataset;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapPartitionFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.util.Collector;

import java.util.Iterator;

import static org.apache.flink.api.java.aggregation.Aggregations.MIN;
import static org.apache.flink.api.java.aggregation.Aggregations.SUM;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args ) throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        DataSet<Tuple3<Integer, String, Double>> input =  env.fromElements(
                new Tuple3<>(1,"1",1.1D),
                new Tuple3<>(1,"1",1.2D),
                new Tuple3<>(1,"1",1.3D),
                new Tuple3<>(2,"2",2.1D),
                new Tuple3<>(1,"2",2.2D),
                new Tuple3<>(0,"2",2.3D),
                new Tuple3<>(2,"2c",2.3D),
                new Tuple3<>(2,"2c",2.3D),
                new Tuple3<>(2,"3c",2.3D),
                new Tuple3<>(2,"4c",2.3D),
                new Tuple3<>(2,"11",2.3D),
                new Tuple3<>(1,"5c",2.4D));
        DataSet<Tuple3<Integer, String, Double>> output = input
                .groupBy(1)        // group DataSet on second field
                .aggregate(SUM, 0) // compute sum of the first field
                .and(MIN, 2);      // compute minimum of the third field

        output.print();
        System.out.println("!!!!!!!!!!!!!");

        DataSet<Tuple3<Integer, String, Double>> output1 = input
                .groupBy(1)        // group DataSet on second field
                .aggregate(SUM, 0);     // compute minimum of the third field

        output1.print();
        System.out.println("!!!!!!!!!!!!!");

        DataSet<Tuple3<Integer, String, Double>> output2 = input
                .groupBy(1)        // group DataSet on second field
                .aggregate(SUM, 0) // compute sum of the first field
                .aggregate(MIN, 2);      // compute minimum of the third field

        output2.print();

        System.out.println("!!!!!!!!!!!!!");
        DataSet<Tuple3<Integer, String, Double>> output3 = input
                .groupBy(1)   // group DataSet on second field
                .minBy(0, 2); // select tuple with minimum values for first and third field.
        output3.print();

        System.out.println("!!!!!!!!!!!!!");
        input.partitionByHash(1).setParallelism(3).mapPartition(new MapPartitionFunction<Tuple3<Integer, String, Double>, String>() {
            @Override
            public void mapPartition(Iterable<Tuple3<Integer, String, Double>> values, Collector<String> out) throws Exception {
                long treadId = Thread.currentThread().getId();
                Iterator<Tuple3<Integer, String, Double>> iterator = values.iterator();
                String key = null;
                while (iterator.hasNext()) {
                    Tuple3<Integer, String, Double> next = iterator.next();
                    key = next.f1;
                    System.out.println("value:"+next+",treadId:"+treadId);
                }
                System.out.println("@@@:"+key+",treadId:"+treadId);
            }
        }).print();

        System.out.println("!!!!!!!!!!!!!");

        input.partitionByRange(1).setParallelism(3).mapPartition(new MapPartitionFunction<Tuple3<Integer, String, Double>, String>() {
            @Override
            public void mapPartition(Iterable<Tuple3<Integer, String, Double>> values, Collector<String> out) throws Exception {
                long treadId = Thread.currentThread().getId();
                Iterator<Tuple3<Integer, String, Double>> iterator = values.iterator();
                String key = null;
                while (iterator.hasNext()) {
                    Tuple3<Integer, String, Double> next = iterator.next();
                    key = next.f1;
                    System.out.println("value:"+next+",treadId:"+treadId);
                }
                System.out.println("@@@:"+key+",treadId:"+treadId);
            }
        }).print();
    }
}
