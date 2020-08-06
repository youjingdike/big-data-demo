package com.xq.apitest.sinktest;

import com.xq.apitest.pojo.SensorReading;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EsSinkTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStreamSource<String> inputStream = env.readTextFile("D:\\code\\bigdatademo\\flinkdemo\\src\\main\\resources\\sensor.txt");

        // 1. 基本转换操作：map成样例类类型
        SingleOutputStreamOperator<SensorReading> dataStream = inputStream.map((MapFunction<String, SensorReading>) value -> {
            String[] split = value.split(",");
            return new SensorReading(split[0].trim(), Long.parseLong(split[1].trim()), Double.parseDouble(split[2].trim()));
        });

        List<HttpHost> hostList = new ArrayList<>();
        hostList.add(new HttpHost("localhost", 9200));

        ElasticsearchSinkFunction<SensorReading> esFun = new ElasticsearchSinkFunction<SensorReading>() {
            private static final long serialVersionUID = -6637877205673353398L;

            @Override
            public void process(SensorReading element, RuntimeContext runtimeContext, RequestIndexer requestIndexer) {
// 首先定义写入es的source
                Map dataSource = new HashMap<String, String>();
                dataSource.put("sensor_id", element.getId());
                dataSource.put("temp", element.getTemperature().toString());
                dataSource.put("ts", element.getTimestamp().toString());

                // 创建index request
                IndexRequest indexRequest = Requests.indexRequest()
                        .index("sensor")
                        .type("data")
                        .source(dataSource);

                // 使用RequestIndexer发送http请求
                requestIndexer.add(indexRequest);

                System.out.println("data " + element + " saved successfully");
            }
        };


        ElasticsearchSink<SensorReading> esSinkBuilder = new ElasticsearchSink.Builder<>(hostList, esFun).build();
        /*esSinkBuilder.setBulkFlushMaxActions(1);

// provide a RestClientFactory for custom configuration on the internally created REST client
        esSinkBuilder.setRestClientFactory(
                restClientBuilder -> {
                    restClientBuilder.setDefaultHeaders(...)
                    restClientBuilder.setMaxRetryTimeoutMillis(...)
                    restClientBuilder.setPathPrefix(...)
                    restClientBuilder.setHttpClientConfigCallback(...)
                }
        );*/
        dataStream.addSink(esSinkBuilder);

        env.execute("test es sink job");
    }
}
