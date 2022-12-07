package com.xq.tst;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class TstEs {
    public static void main(String[] args) throws IOException {
        String securityUser = "es-app:ENUJZcBfyR7Jqr5w";
        HttpHost[] httpHosts = {
                new HttpHost("10.69.74.220",19201,"http")
        };

        RestClientBuilder builder = RestClient.builder(httpHosts);
        byte[] encodedAuth = Base64.encodeBase64(securityUser.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        builder.setDefaultHeaders(new BasicHeader[]{new BasicHeader("Authorization", authHeader)});
        RestHighLevelClient restHighLevelClint = new RestHighLevelClient(builder);
        // 1.构建SearchRequest检索请求
        // 专门用来进行全文检索、关键字检索的API
        SearchRequest searchRequest = new SearchRequest("index");
        searchRequest.types("type");

        // 2.创建一个SearchSourceBuilder专门用于构建查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 3.使用QueryBuilders.multiMatchQuery构建一个查询条件（搜索title、jd），并配置到SearchSourceBuilder
//        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("dd", "title", "jd");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //TODO: 注意value不能为空，为空会抛出异常
        boolQueryBuilder.must(QueryBuilders.termQuery("name", "value"));
        boolQueryBuilder.must(QueryBuilders.termQuery("id", 5));
        boolQueryBuilder.must(QueryBuilders.termQuery("d", "2022-08-20"));
        boolQueryBuilder.must(QueryBuilders.termQuery("t", "15:30:33"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse("2022-08-20 15:30:33", formatter);
        boolQueryBuilder.must(QueryBuilders.termQuery("dt", dateTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()));

        // 3.1 将查询条件设置到查询请求构建器中
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(500);

        // 4.调用SearchRequest.source将查询条件设置到检索请求
//        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
        searchRequest.scroll(TimeValue.timeValueMinutes(5));
        searchRequest.source(searchSourceBuilder);
        /*Scroll scroll = searchRequest.scroll();
        SearchRequest scroll1 = searchRequest.scroll(scroll);*/
        // 5.执行RestHighLevelClient.search发起请求
        SearchResponse searchResponse = restHighLevelClint.search(searchRequest, RequestOptions.DEFAULT);
        long totalHits = searchResponse.getHits().totalHits;
        if (totalHits==0) {
            return;
        }

        long searchSize = 0L;
        SearchHit[] hitArray = searchResponse.getHits().getHits();
        searchSize += hitArray.length;

        for (int i = 0; i < hitArray.length; i++) {
            Map<String, Object> sourceAsMap = hitArray[i].getSourceAsMap();
            //TODO 将结果保存
        }

        String scrollId = searchResponse.getScrollId();
        while (searchSize < totalHits) {
            searchResponse = scrollSearch(scrollId, restHighLevelClint);
            searchSize += searchResponse.getHits().getHits().length;
            //TODO 将结果保存
        }

        ClearScrollRequest request = new ClearScrollRequest();
        request.addScrollId(scrollId);
        ClearScrollResponse response = restHighLevelClint.clearScroll(request,RequestOptions.DEFAULT);
        response.isSucceeded();
    }

    private static SearchResponse scrollSearch(String scrollId,RestHighLevelClient restHighLevelClient) {
        SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
        searchScrollRequest.scroll(TimeValue.timeValueMinutes(5));
        SearchResponse searchResponse;
        // 使用RestHighLevelClient发送scroll请求
        try {
            searchResponse = restHighLevelClient.scroll(searchScrollRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return searchResponse;
    }
}
