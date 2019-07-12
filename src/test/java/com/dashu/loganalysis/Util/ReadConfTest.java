package com.dashu.loganalysis.Util;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @Description
 * @Author: xuyouchang
 * @Date 2019/7/11 下午7:05
 **/
public class ReadConfTest {

    @Test
    public void readYml() {
        ReadConf readConf = new ReadConf("conf/email.yml");
        Map conf = readConf.readYml();
        Map email = (Map) conf.get("tidbServer");
        List list = (List) email.get("to");
    }
}