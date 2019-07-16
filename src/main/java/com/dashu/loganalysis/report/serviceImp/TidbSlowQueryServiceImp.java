package com.dashu.loganalysis.report.serviceImp;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.dashu.loganalysis.report.dao.TidbSlowQueryRepository;
import com.dashu.loganalysis.report.service.EmailService;
import com.dashu.loganalysis.report.service.TidbSlowQueryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author: xuyouchang
 * @Date 2019/7/15 上午9:35
 **/
@Service
public class TidbSlowQueryServiceImp implements TidbSlowQueryService {
    private static final Logger logger = LoggerFactory.getLogger(TidbSlowQueryServiceImp.class);
    private static final String TIDB_SLOW_QUERY = "log_tidb_slow_query-2019.07.16_test";
    @Resource
    private TidbSlowQueryRepository tidbSlowQueryRepository;
    @Resource
    private EmailService emailService;

    @Override
    public void tidbSlowQueryWeekReport(Map emailConf) {
        List<Map<String, Object>> userQueryTimeList = tidbSlowQueryRepository.sumQueryTimeByUser(TIDB_SLOW_QUERY);
        logger.debug("userQueryTimeList: {} ", userQueryTimeList);
        String emailContent = constructTidbSlowQueryWeekReportEmail("用户慢查询统计", userQueryTimeList);
        logger.debug("email content: {}", emailContent);
        emailService.sendEmail(emailConf, "tidb slow query week report", emailContent, "html");
    }

    // 构造邮件格式
    private String constructTidbSlowQueryWeekReportEmail(String titlename, List<Map<String, Object>> mapList) {
        String title = "<h2>TITLENAME</h2>";
        String table = "<table border='1'>ROWS</table>";
        String rows = "<tr><th>用户</th><th>慢查询总耗时(s)</th><th>慢查询次数</th></tr>";
        String row = "<tr><td>user</td><td>total</td><td>queryTimes</td></tr>";
        for (Map map : mapList) {
            rows = rows + row.replace("user", map.get("key").toString()).replace("total", map.get("value").toString())
                    .replace("queryTimes", map.get("doc_count").toString());
        }
        title = title.replace("TITLENAME", titlename);
        table = table.replace("ROWS", rows);
        String content = title + table;
        return content;
    }
}
