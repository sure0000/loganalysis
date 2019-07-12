package com.dashu.loganalysis.report;

import com.dashu.loganalysis.Util.ReadConf;
import com.dashu.loganalysis.report.service.TidbReportService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Description 报表发送
 * @Author: xuyouchang
 * @Date 2019/7/11 下午4:39
 **/
@Component
public class Reporter {
    @Resource
    private TidbReportService tidbReportService;

//    @Scheduled(cron = "30 9 * * 4 *")
    public void tidbServerWeekReport() {
        ReadConf readConf = new ReadConf("conf/email.yml");
        Map<String,Object> conf = readConf.readYml();
        tidbReportService.tidbServerWeekReport((Map) conf.get("tidbServer"));
    }
}
