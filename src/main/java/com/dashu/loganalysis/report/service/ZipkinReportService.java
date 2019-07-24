package com.dashu.loganalysis.report.service;

import java.util.Map;

/**
 * @Description zipkin 周报的相关服务
 * @Author: xuyouchang
 * @Date 2019/7/23 下午1:42
 **/
public interface ZipkinReportService {

    /**
     * zipkin 统计周报
     * @param emailConf email 配置
     */
    void zipkinWeekReport(Map emailConf);
}
