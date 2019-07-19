package com.dashu.loganalysis.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

/**
 * @Description 读取 YML 文件
 * @Author: xuyouchang
 * @Date 2019/7/11 下午2:31
 **/
public class ReadConf {
    /**
     * YML 文件路径
     */
    private String filePath;

    private static final Logger logger = LoggerFactory.getLogger(ReadConf.class);
    private static final ResourceLoader resourceLoader = new DefaultResourceLoader();

    public ReadConf(String filePath) {
        this.filePath = "classpath:" + filePath;
    }

    /**
     * 读取YML配置文件
     * @return 配置文件内容
     */
    public Map readYml() {
        Yaml yaml = new Yaml();
        Resource resource = resourceLoader.getResource(this.filePath);
        try {
            // springboot jar内部读取需要用到文件流 不能使用 File
            InputStream inputStream = resource.getInputStream();
//            File f = ResourceUtils.getFile(this.filePath);
            return yaml.load(inputStream);
        } catch (IOException e) {
            logger.error("{} 读取异常 {}", this.filePath, e);
            return null;
        }

    }

}
