package com.dashu.loganalysis.report.serviceImp;

import com.dashu.loganalysis.report.dao.TidbServerRepository;
import com.dashu.loganalysis.report.service.EmailService;
import com.dashu.loganalysis.report.service.TidbReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Description tidb server 日志服务实现
 * @Author: xuyouchang
 * @Date 2019/7/10 上午10:57
 **/
@Service
public class TidbReportServiceImp implements TidbReportService {

    private static final String LOG_TIDB_SERVER = "log_tidb_server*";
    private static final String EMAIL_TYPE_HTML = "html";
    private static final String SUBJECT_WEEK_REPORT = "Tidb Server 日志周报";
    private static final Logger logger = LoggerFactory.getLogger(TidbReportServiceImp.class);

    @Resource
    private EmailService emailService;
    @Resource
    private TidbServerRepository tidbServerRepository;

    @Override
    public void tidbServerWeekReport(Map emailConf) {

        logger.info("开始 tidbServerWeekReport");
        List<Map<String, Object>> warnList = tidbServerRepository.countAndFilterByLoglevel(LOG_TIDB_SERVER, "WARN");
        List<Map<String, Object>> errorList = tidbServerRepository.countAndFilterByLoglevel(LOG_TIDB_SERVER, "ERROR");
        String warnContent = constructEmailContent("WARN 类型日志统计", warnList);
        String errorContent = constructEmailContent("ERROR 类型日志统计", errorList);
        String emailContent = warnContent + errorContent;

        emailService.sendEmail(emailConf, SUBJECT_WEEK_REPORT, emailContent, EMAIL_TYPE_HTML);
        logger.info("结束 tidbServerWeekReport");
    }

    // 构造邮件格式
    public String constructEmailContent(String titleName, List<Map<String, Object>> mapList) {
        String title = "<h2>TITLENAME</h2>";
        String table = "<table border='1'>ROWS</table>";
        String row = "<tr><td>LOCATION</td><td>NUM</td></tr>";
        String rows = "<tr><th>操作类型</th><th>出现次数</th></tr>";
        for (Map map : mapList) {
            rows = rows + row.replace("LOCATION", map.get("key").toString())
                    .replace("NUM", map.get("value").toString());
        }
        title = title.replace("TITLENAME", titleName);
        table = table.replace("ROWS", rows);
        return title + table;
    }
}
