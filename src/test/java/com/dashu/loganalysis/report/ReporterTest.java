package com.dashu.loganalysis.report;

import com.dashu.loganalysis.LoganalysisApplicationTests;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * @Description
 * @Author: xuyouchang
 * @Date 2019/7/11 下午7:58
 **/
public class ReporterTest extends LoganalysisApplicationTests {
    @Resource
    private Reporter reporter;

    @Test
    public void tidbServerWeekReport() {
        reporter.tidbServerWeekReport();
    }
}