package com.dashu.loganalysis.report.service;

import javax.mail.Address;
import java.util.Map;

/**
 * @Description
 * @Author: xuyouchang
 * @Date 2019/7/10 上午10:55
 **/
public interface TidbReportService {
    /**
     * tidb server 日志周报
     * @param emailConf 邮件配置
     */
    void tidbServerWeekReport(Map emailConf);
}
