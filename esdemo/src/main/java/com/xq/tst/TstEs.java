package com.xq.tst;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.Map;

public class TstEs {
    public static void main(String[] args) throws IOException {
        HttpHost[] httpHosts = {};
        RestHighLevelClient restHighLevelClint = new RestHighLevelClient(RestClient.builder(httpHosts));
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

        // 3.1 将查询条件设置到查询请求构建器中
        searchSourceBuilder.query(boolQueryBuilder);

        // 4.调用SearchRequest.source将查询条件设置到检索请求
//        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
        searchRequest.source(searchSourceBuilder);

        // 5.执行RestHighLevelClient.search发起请求
        SearchResponse searchResponse = restHighLevelClint.search(searchRequest);
        SearchHit[] hitArray = searchResponse.getHits().getHits();
        for (int i = 0; i < hitArray.length; i++) {
            Map<String, Object> sourceAsMap = hitArray[i].getSourceAsMap();
            sourceAsMap.get("name");
            sourceAsMap.get("dddd");
        }
    }
}
