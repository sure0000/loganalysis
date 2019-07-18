package com.dashu.loganalysis.report.service;

import java.util.Map;

/**
 * @Description TIDB 慢查询日志服务
 * @Author: xuyouchang
 * @Date 2019/7/15 上午9:34
 **/
public interface TidbSlowQueryService {
    /**
     * tidb 慢查询周报
     *
     * @param emailConf 邮件配置
     */
    void tidbSlowQueryWeekReport(Map emailConf);

    /**
     * tidb 慢查询日报
     *
     * @param emailConf 邮件配置
     */
    void tidbSlowQueryDayReport(Map emailConf);
}
