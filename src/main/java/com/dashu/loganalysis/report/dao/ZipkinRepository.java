package com.dashu.loganalysis.report.dao;

import org.elasticsearch.search.SearchHit;

import java.util.List;
import java.util.Map;

/**
 * @Description zipkin 索引的基本查询操作
 * @Author: xuyouchang
 * @Date 2019/7/23 下午1:46
 **/
public interface ZipkinRepository {

    /**
     * 获取 top 10 duration
     * @param index 索引
     * @param field 字段名
     * @return 搜索结果
     */
    SearchHit[] getTop(String index, String field, int size);

    /**
     * 根据字段统计文档数
     * @param index 索引名
     * @param field 字段名
     * @param size 返回数量
     * @return
     */
    List<Map<String, Object>> getCountByField(String index, String field, int size);
}
