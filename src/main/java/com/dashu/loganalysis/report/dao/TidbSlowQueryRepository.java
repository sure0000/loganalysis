package com.dashu.loganalysis.report.dao;

import java.util.List;
import java.util.Map;

/**
 * @Description 索引 TidbSlowQuery 相关操作
 * @Author: xuyouchang
 * @Date 2019/7/15 上午9:36
 **/
public interface TidbSlowQueryRepository {
    List<Map<String, Object>> sumQueryTimeByUser(String index);
}
