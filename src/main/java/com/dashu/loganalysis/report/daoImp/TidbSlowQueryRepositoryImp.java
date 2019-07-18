package com.dashu.loganalysis.report.daoImp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dashu.loganalysis.report.dao.EsInstance;
import com.dashu.loganalysis.report.dao.TidbSlowQueryRepository;

import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder.Order;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class TidbSlowQueryRepositoryImp implements TidbSlowQueryRepository {
    private static final Logger logger = LoggerFactory.getLogger(TidbSlowQueryRepositoryImp.class);

    @Override
    @SuppressWarnings(value = "unchecked")
    public List<Map<String, Object>> sumQueryTimeByUser(String index) {
        RestHighLevelClient client = EsInstance.INSTANCE.connect();
        SearchRequest request = new SearchRequest(index);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("by_user").field("User");
        termsAggregationBuilder.subAggregation(AggregationBuilders.sum("sum_query_time").field("Query_time"));
        termsAggregationBuilder.subAggregation(AggregationBuilders.avg("avg_query_time").field("Query_time"));
        termsAggregationBuilder.order(BucketOrder.aggregation("avg_query_time", false));
        termsAggregationBuilder.size(100);
        searchSourceBuilder.aggregation(termsAggregationBuilder);

        request.source(searchSourceBuilder);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Aggregations aggregations = response.getAggregations();
            Terms terms = aggregations.get("by_user");
            List<Terms.Bucket> bucketList = (List<Terms.Bucket>) terms.getBuckets();
            List<Map<String, Object>> userQueryTimeList = new ArrayList<>();
            for (Terms.Bucket bucket : bucketList) {
                Map<String, Object> userQueryTime = new HashMap<>();
                Sum sumQueryTime = bucket.getAggregations().get("sum_query_time");
                Avg avgQueryTime = bucket.getAggregations().get("avg_query_time");
                userQueryTime.put("key", bucket.getKey());
                userQueryTime.put("value", sumQueryTime.value());
                userQueryTime.put("doc_count", bucket.getDocCount());
                userQueryTime.put("avg", (float) avgQueryTime.value());
                userQueryTimeList.add(userQueryTime);
            }
            return userQueryTimeList;
        } catch (IOException e) {
            logger.error("求和失败 {} ", e);
            return null;
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                logger.error("client 关闭失败 {}", e);
            }
        }
    }

    @Override
    public List<SearchHit[]> getQueryTimeLargeThan5s(String index) {
        RestHighLevelClient client = EsInstance.INSTANCE.connect();
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
        SearchRequest request = new SearchRequest(index);
        request.scroll(scroll);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("Query_time").gte(5));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("@timestamp").from("now-1d").to("now"));
        searchSourceBuilder.sort(new FieldSortBuilder("Query_time").order(SortOrder.DESC));
        searchSourceBuilder.query(boolQueryBuilder);

        request.source(searchSourceBuilder);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            String scrollId = response.getScrollId();
            SearchHit[] searchHits = response.getHits().getHits();
            List<SearchHit[]> batchHits = new ArrayList<>();
            batchHits.add(searchHits);
            while (searchHits != null && searchHits.length > 0) {
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(scroll);
                response = client.scroll(scrollRequest, RequestOptions.DEFAULT);
                scrollId = response.getScrollId();
                searchHits = response.getHits().getHits();
                batchHits.add(searchHits);
            }

            ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.addScrollId(scrollId);
            ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
            if (!clearScrollResponse.isSucceeded()) {
                logger.warn("清除 scroll response 未成功");
            }
            return batchHits;
        } catch (IOException e) {
            logger.error("获取 response 失败 {}", e);
            return null;
        }
    }

}