package com.dashu.loganalysis.report.daoImp;

import com.dashu.loganalysis.report.dao.EsInstance;
import com.dashu.loganalysis.report.dao.ZipkinRepository;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHitsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description ZipkinRepository 实现类
 * @Author: xuyouchang
 * @Date 2019/7/23 下午1:50
 **/
@Repository
public class ZipkinRepositoryImp implements ZipkinRepository {
    private static final Logger logger = LoggerFactory.getLogger(ZipkinRepositoryImp.class);

    @Override
    public SearchHit[] getTop(String index, String filed, int size) {
        RestHighLevelClient client = EsInstance.INSTANCE.connect();
        SearchRequest request = new SearchRequest(index);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("timestamp_millis").from("now-7d").to("now"));
        TopHitsAggregationBuilder topHitsAggregationBuilder = AggregationBuilders.topHits("top").docValueField(filed);
        topHitsAggregationBuilder.size(size);
        topHitsAggregationBuilder.sort(filed, SortOrder.DESC);
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.aggregation(topHitsAggregationBuilder);
        request.source(searchSourceBuilder);

        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            TopHits topHits = response.getAggregations().get("top");
            return topHits.getHits().getHits();
        } catch (IOException e) {
            logger.error("获取 top 聚合结果异常 {}", e);
            return null;
        }finally {
            try {
                client.close();
            } catch (IOException e) {
                logger.error("top client 关闭异常 {}", e);
            }
        }
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public List<Map<String, Object>> getCountByField(String index, String field, int size) {
        RestHighLevelClient client = EsInstance.INSTANCE.connect();
        SearchRequest request = new SearchRequest(index);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("timestamp_millis").from("now-7d").to("now"));
        searchSourceBuilder.query(boolQueryBuilder);

        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("by_field").field(field);
        termsAggregationBuilder.subAggregation(AggregationBuilders.count("count_field").field(field));
        termsAggregationBuilder.order(BucketOrder.count(false));
        termsAggregationBuilder.size(size);
        searchSourceBuilder.aggregation(termsAggregationBuilder);
        request.source(searchSourceBuilder);

        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Terms termsAggration = response.getAggregations().get("by_field");
            List<Terms.Bucket> buckets = (List<Terms.Bucket>)termsAggration.getBuckets();
            List<Map<String, Object>> countList = new ArrayList<>();
            for (Terms.Bucket bucket : buckets) {
                Map<String, Object> countMap = new HashMap<>();
                ValueCount valueCount = bucket.getAggregations().get("count_field");
                countMap.put("key", bucket.getKey());
                countMap.put("value", valueCount.getValue());
                countList.add(countMap);
            }
            return countList;
        } catch (IOException e) {
            logger.error("get count 异常 {}", e);
            return null;
        }finally {
            try {
                client.close();
            } catch (IOException e) {
                logger.error("client 关闭异常 {}", e);
            }
        }

    }
}
