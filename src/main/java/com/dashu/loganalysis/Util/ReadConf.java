package com.dashu.loganalysis.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * @Description 读取 YML 文件
 * @Author: xuyouchang
 * @Date 2019/7/11 下午2:31
 **/
public class ReadConf {
    private String filePath = "classpath:TEMPLATE";
    private static final Logger logger = LoggerFactory.getLogger(ReadConf.class);

    public ReadConf(String filePath) {
        this.filePath = this.filePath.replace("TEMPLATE", filePath);
    }

    /**
     * 读取YML配置文件
     * @return 配置文件内容
     */
    public Map readYml() {
        Yaml yaml = new Yaml();
        File f;
        Object result;
        try {
            f = ResourceUtils.getFile(this.filePath);
            result = yaml.load(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            logger.error("{} 不存在 {}", this.filePath, e);
            return null;
        }
        Map resultAsListMap= (Map) result;
        return resultAsListMap;
    }

}
