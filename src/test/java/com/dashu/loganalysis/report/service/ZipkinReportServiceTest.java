package com.dashu.loganalysis.report.service;

import com.dashu.loganalysis.LoganalysisApplicationTests;
import com.dashu.loganalysis.Util.ReadConf;
import org.junit.Test;

import javax.annotation.Resource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @Description
 * @Author: xuyouchang
 * @Date 2019/7/23 下午7:13
 **/
public class ZipkinReportServiceTest extends LoganalysisApplicationTests {

    @Resource
    private ZipkinReportService zipkinReportService;
    @Test
    public void zipkinWeekReport() {
        ReadConf readConf = new ReadConf("conf/email.yml");
        zipkinReportService.zipkinWeekReport((Map) readConf.readYml().get("zipkin"));
    }

    @Test
    public void long2Timestamp() {
        String t = "1563765300161";
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(new Date(Long.valueOf(t))));
    }
}