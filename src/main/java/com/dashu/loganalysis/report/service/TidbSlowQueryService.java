package com.dashu.loganalysis.report.service;

import java.util.Map;

/**
 * @Description tidb 慢查询日志服务
 * @Author: xuyouchang
 * @Date 2019/7/15 上午9:34
 **/
public interface TidbSlowQueryService {
    
    void tidbSlowQueryWeekReport(Map emailConf);
}
