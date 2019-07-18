package com.dashu.loganalysis.report.service;

import java.util.Map;

/**
 * @Description Tidb 索引相关服务
 * @Author: xuyouchang
 * @Date 2019/7/10 上午10:55
 **/
public interface TidbReportService {
    /**
     * tidb server 日志周报
     *
     * @param emailConf 邮件配置
     */
    void tidbServerWeekReport(Map emailConf);
}
