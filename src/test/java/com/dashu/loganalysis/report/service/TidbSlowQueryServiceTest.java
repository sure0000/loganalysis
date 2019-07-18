package com.dashu.loganalysis.report.service;

import java.util.Map;

import javax.annotation.Resource;

import com.dashu.loganalysis.LoganalysisApplicationTests;
import com.dashu.loganalysis.Util.ReadConf;

import org.junit.Test;

public class TidbSlowQueryServiceTest extends LoganalysisApplicationTests {
    @Resource
    private TidbSlowQueryService tidbSlowQueryService;
    @Resource
    private EmailService emailService;
    
    @Test
    public void tidbSlowQueryDayReportTest() {
        ReadConf readConf = new ReadConf("conf/email.yml");
        Map conf = readConf.readYml();
        tidbSlowQueryService.tidbSlowQueryDayReport((Map)conf.get("tidbSlowQuery"));
    }

    @Test
    public void tidbSlowQueryWeekReport() {
        ReadConf readConf = new ReadConf("conf/email.yml");
        Map conf = readConf.readYml();
        tidbSlowQueryService.tidbSlowQueryWeekReport((Map)conf.get("tidbSlowQuery"));
    }
     
}