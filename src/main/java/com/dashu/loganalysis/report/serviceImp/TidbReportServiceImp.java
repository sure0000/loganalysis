package com.dashu.loganalysis.report.serviceImp;

import com.dashu.loganalysis.report.dao.TidbServerRepository;
import com.dashu.loganalysis.report.service.EmailService;
import com.dashu.loganalysis.report.service.TidbReportService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description tidb server 日志服务实现
 * @Author: xuyouchang
 * @Date 2019/7/10 上午10:57
 **/
@Service
public class TidbReportServiceImp implements TidbReportService {
    @Resource
    private EmailService emailService;
    @Resource
    private TidbServerRepository tidbServerRepository;

    @Override
    public void tidbServerWeekReport(Map emailConf) {

        List<Map<String, Object>> warnList = tidbServerRepository.countAndFilterByLoglevel("log_tidb_server-2019.07.16_test", "WARN");
        List<Map<String, Object>> errorList = tidbServerRepository.countAndFilterByLoglevel("log_tidb_server-2019.07.16_test",
                "ERROR");
        String warnContent = constructEmailContent("WARN 类型日志统计", warnList);
        String errorContent = constructEmailContent("ERROR 类型日志统计", errorList);
        String emailContent = warnContent + errorContent;

        emailService.sendEmail(emailConf, "Tidb Server Week Report", emailContent, "HTML");
    }

    // 构造邮件格式
    public String constructEmailContent(String titleName, List<Map<String, Object>> mapList) {
        String title = "<h2>TITLENAME</h2>";
        String table = "<table border='1'>ROWS</table>";
        String row = "<tr><td>LOCATION</td><td>NUM</td></tr>";
        String rows = "<tr><th>操作类型</th><th>出现次数</th></tr>";
        for (Map map : mapList) {
            rows = rows
                    + row.replace("LOCATION", map.get("key").toString()).replace("NUM", map.get("value").toString());
        }
        title = title.replace("TITLENAME", titleName);
        table = table.replace("ROWS", rows);
        String content = title + table;
        return content;
    }
}
