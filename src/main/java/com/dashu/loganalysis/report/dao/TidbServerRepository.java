package com.dashu.loganalysis.report.dao;

import java.util.List;
import java.util.Map;

/**
 * @Description log tidb server 索引的相关操作
 * @Author: xuyouchang
 * @Date 2019/7/10 上午11:00
 **/
public interface TidbServerRepository {
    /**
     * 根据日志级别过滤并 count
     * @param index 索引名称
     * @param loglevel 日志级别
     * @return 查询结果
     */
    List<Map<String,Object>> countAndFilterByLoglevel(String index, String loglevel);
}

