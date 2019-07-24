package com.dashu.loganalysis.report.dao;

import com.dashu.loganalysis.LoganalysisApplicationTests;
import org.elasticsearch.search.SearchHit;
import org.junit.Test;

import javax.annotation.Resource;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @Description
 * @Author: xuyouchang
 * @Date 2019/7/23 下午2:07
 **/
public class ZipkinRepositoryTest extends LoganalysisApplicationTests {
    @Resource
    private ZipkinRepository zipkinRepository;

    @Test
    public void getTopDuration() {
        SearchHit[] searchHits = zipkinRepository.getTop("zipkin*", "duration", 10);
        SearchHit[] searchHits2 = zipkinRepository.getTop("zipkin*", "name", 10);
    }

    @Test
    public void getCountByField() {
        List<Map<String, Object>> mapList = zipkinRepository.getCountByField("zipkin*", "localEndpoint.serviceName", 100);
        List<Map<String, Object>> mapList1 = zipkinRepository.getCountByField("zipkin*", "name", 100);
    }
}