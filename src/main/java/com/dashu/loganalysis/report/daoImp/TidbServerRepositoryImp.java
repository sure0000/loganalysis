package com.dashu.loganalysis.report.daoImp;


import com.dashu.loganalysis.report.dao.EsInstance;
import com.dashu.loganalysis.report.dao.TidbServerRepository;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;

import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author: xuyouchang
 * @Date 2019/7/10 上午11:13
 **/
@Repository
public class TidbServerRepositoryImp implements TidbServerRepository {
    private static final Logger logger = LoggerFactory.getLogger(TidbServerRepositoryImp.class);

    @Override
    public List<Map<String,Object>> countAndFilterByLoglevel(String index, String loglevel) {
        RestHighLevelClient client = EsInstance.INSTANCE.connect();
        SearchRequest request = new SearchRequest(index);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // boolQueryBuilder
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.filter(QueryBuilders.matchQuery("loglevel",loglevel));
        searchSourceBuilder.query(boolQueryBuilder);

        // termsAggregationBuilder
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("by_location")
                .field("location");
        termsAggregationBuilder.subAggregation(AggregationBuilders.count("count_location").field("location"));
        termsAggregationBuilder.order(BucketOrder.count(false));
        searchSourceBuilder.aggregation(termsAggregationBuilder);

        request.source(searchSourceBuilder);
        try {
            SearchResponse response = client.search(request);
            Aggregations aggregations = response.getAggregations();
            Terms byActionAggregation = aggregations.get("by_location");
            List<Terms.Bucket> buckets = (List<Terms.Bucket>)byActionAggregation.getBuckets();
            List<Map<String,Object>> warnCountList = new ArrayList<>();
            for (Terms.Bucket bucket : buckets) {
                String key = bucket.getKeyAsString();
                ValueCount value = bucket.getAggregations().get("count_location");
                Map<String, Object> warnMap = new HashMap<>();
                warnMap.put("key", key);
                warnMap.put("value", value.getValue());
                warnCountList.add(warnMap);
            }
            return warnCountList;
        } catch (IOException e) {
            logger.error("warn Count fail {}",e);
            return null;
        }finally {
            try {
                client.close();
            } catch (IOException e) {
                logger.error("client close fail {}", e);
            }
        }

    }

    private SearchHits filterByLocationAndLoglevel(RestHighLevelClient client,
                                                     String index,
                                                     String location,
                                                     String loglevel) throws IOException {
        SearchRequest request = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.filter(QueryBuilders.matchQuery("loglevel", loglevel))
                .filter(QueryBuilders.matchQuery("location", location));
        searchSourceBuilder.query(boolQueryBuilder);
        request.source(searchSourceBuilder);

        SearchResponse response = client.search(request);
        SearchHits searchHits = response.getHits();
        return searchHits;
    }
}
