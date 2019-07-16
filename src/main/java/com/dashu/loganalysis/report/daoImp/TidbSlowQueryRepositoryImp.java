package com.dashu.loganalysis.report.daoImp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dashu.loganalysis.report.dao.EsInstance;
import com.dashu.loganalysis.report.dao.TidbSlowQueryRepository;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;

import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class TidbSlowQueryRepositoryImp implements TidbSlowQueryRepository {
    private static final Logger logger = LoggerFactory.getLogger(TidbSlowQueryRepositoryImp.class);

    @Override
    public List<Map<String, Object>> sumQueryTimeByUser(String index) {
        RestHighLevelClient client = EsInstance.INSTANCE.connect();
        SearchRequest request = new SearchRequest(index);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("by_user").field("User");
        termsAggregationBuilder.subAggregation(AggregationBuilders.sum("sum_query_time").field("Query_time"));
        termsAggregationBuilder.order(BucketOrder.aggregation("sum_query_time", false));
        searchSourceBuilder.aggregation(termsAggregationBuilder);

        request.source(searchSourceBuilder);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Aggregations aggregations = response.getAggregations();
            Terms terms = aggregations.get("by_user");
            List<Terms.Bucket> bucketList = (List<Terms.Bucket>) terms.getBuckets();
            List<Map<String,Object>> userQueryTimeList = new ArrayList<>();
            for (Terms.Bucket bucket : bucketList) {
                Map<String, Object> userQueryTime = new HashMap<>();
                Sum sumQueryTime = bucket.getAggregations().get("sum_query_time");
                userQueryTime.put("key", bucket.getKey());
                userQueryTime.put("value", sumQueryTime.value());
                userQueryTime.put("doc_count", bucket.getDocCount());
                userQueryTimeList.add(userQueryTime);
            }     
            return userQueryTimeList;      
        } catch (IOException e) {
            logger.error("sum user query time fail {} ", e);
            return null;
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                logger.error("es client close fail", e);
            }
        }
    }
    
}