package com.dashu.loganalysis.report.dao;

import java.util.List;
import java.util.Map;

import org.elasticsearch.search.SearchHit;

/**
 * @Description 索引 TidbSlowQuery 相关操作
 * @Author: xuyouchang
 * @Date 2019/7/15 上午9:36
 **/
public interface TidbSlowQueryRepository {
    /**
     * 不同用户查询时间求和
     * @param index 索引名
     * @return 求和结果
     */
    List<Map<String, Object>> sumQueryTimeByUser(String index);
    
    /**
     * 获取查询时间大于 5s
     * @param index 索引名
     * @return 查询结果
     */
    List<SearchHit[]> getQueryTimeLargeThan5s(String index);
}
