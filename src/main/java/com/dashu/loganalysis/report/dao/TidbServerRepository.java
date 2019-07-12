package com.dashu.loganalysis.report.dao;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author: xuyouchang
 * @Date 2019/7/10 上午11:00
 **/
public interface TidbServerRepository {
    List<Map<String,Object>> countAndFilterByLoglevel(String index, String loglevel);
}

