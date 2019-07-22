package com.dashu.loganalysis.report;

import com.dashu.loganalysis.Util.ReadConf;
import com.dashu.loganalysis.report.service.TidbReportService;
import com.dashu.loganalysis.report.service.TidbSlowQueryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.naming.ConfigurationException;
import java.util.Map;

/**
 * @Description 报表发送
 * @Author: xuyouchang
 * @Date 2019/7/11 下午4:39
 **/
@Component
public class Reporter {
    private static final Logger logger = LoggerFactory.getLogger(Reporter.class);

    private static final String CONF_NAME_TIDB_SERVER = "tidbServer";
    private static final String CONF_NAME_TIDB_SLOW_QUERY = "tidbSlowQuery";

    @Resource
    private TidbReportService tidbReportService;
    @Resource
    private TidbSlowQueryService tidbSlowQueryService;

    /**
     * 周报：每周五 9：30 发送
     */
    @Scheduled(cron = "0 30 9 ? * 6")
    public void weekReport() {
        logger.info("发送 9：30 周报");
        tidbReportService.tidbServerWeekReport(getEmailConf(CONF_NAME_TIDB_SERVER));
        tidbSlowQueryService.tidbSlowQueryWeekReport(getEmailConf(CONF_NAME_TIDB_SLOW_QUERY));
    }

    /**
     * 日报：每天 9：30 发送
     */
    @Scheduled(cron = "0 30 9 * * ?")
//    @Scheduled(cron = "0/30 * * * * *")
    public void dayReport() {
        logger.info("发送 9:30 日报");
        tidbSlowQueryService.tidbSlowQueryDayReport(getEmailConf(CONF_NAME_TIDB_SLOW_QUERY));
    }


    /**
     * 获取对应邮件配置
     * @param confName 具体服务配置名
     * @return map 格式的配置
     */
    @SuppressWarnings(value = "unckecked")
    private Map<String, Object> getEmailConf(String confName) {
        ReadConf readConf = new ReadConf("conf/email.yml");
        Map<String, Object> conf = readConf.readYml();
        try {
            Map<String, Object> emailConf = (Map<String, Object>) conf.get(confName);
            return emailConf;
        } catch (Exception e) {
            logger.error("{} email 配置出错，请重新配置 {}", confName, e);
            return null;
        }

    }
}
