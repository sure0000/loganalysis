package com.dashu.loganalysis.report.dao;

import com.dashu.loganalysis.LoganalysisApplicationTests;
import org.junit.Test;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @Description
 * @Author: xuyouchang
 * @Date 2019/7/11 下午2:14
 **/
public class TidbServerRepositoryTest extends LoganalysisApplicationTests {
    @Resource
    private TidbServerRepository tidbServerRepository;

    @Test
    public void warnCount() {
        //        TidbServerRepository tidbServerRepository = new TidbServerRepositoryImp();
        List<Map<String,Object>> mapList = tidbServerRepository.countAndFilterByLoglevel("log_tidb_server*", "WARN");
        for (Map map : mapList){
            System.out.println(map.get("key") + " " + map.get("value"));
        }
    }
}