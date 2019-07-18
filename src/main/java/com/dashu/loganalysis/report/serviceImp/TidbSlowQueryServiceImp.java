package com.dashu.loganalysis.report.serviceImp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.dashu.loganalysis.report.dao.TidbSlowQueryRepository;
import com.dashu.loganalysis.report.service.EmailService;
import com.dashu.loganalysis.report.service.TidbSlowQueryService;

import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @Description TidbSlowQueryService 实现类
 * @Author: xuyouchang
 * @Date 2019/7/15 上午9:35
 **/
@Service
public class TidbSlowQueryServiceImp implements TidbSlowQueryService {

    private static final Logger logger = LoggerFactory.getLogger(TidbSlowQueryServiceImp.class);

    private static final String INDEX_OF_TIDB_SLOW_QUERY = "log_tidb_slow_query*";
    private static final String SUBJECT_OF_WEEK_REPORT = "tidb 慢查询周报";
    private static final String SUBJECT_OF_DAY_REPORT = "tidb 慢查询日报";
    private static final String EMAIL_TYPE_HTML = "html";

    @Resource
    private TidbSlowQueryRepository tidbSlowQueryRepository;
    @Resource
    private EmailService emailService;

    @Override
    public void tidbSlowQueryWeekReport(Map emailConf) {
        List<Map<String, Object>> userQueryTimeList = tidbSlowQueryRepository.sumQueryTimeByUser(INDEX_OF_TIDB_SLOW_QUERY);
        String emailContent = constructWeekReportEmail(userQueryTimeList);
        emailService.sendEmail(emailConf, SUBJECT_OF_WEEK_REPORT, emailContent, EMAIL_TYPE_HTML);
    }

    @Override
    public void tidbSlowQueryDayReport(Map emailConf) {
        List<SearchHit[]> searchHitArrayList = tidbSlowQueryRepository.getQueryTimeLargeThan5s(INDEX_OF_TIDB_SLOW_QUERY);
        if (searchHitArrayList != null || searchHitArrayList.size() != 0) {
            List<Map> hitList = new ArrayList<>();
            for (SearchHit[] searchHitArray : searchHitArrayList) {
                for (SearchHit hit : searchHitArray) {
                    hitList.add(hit.getSourceAsMap());
                }
            }
            String emailContent = constructDayReportEmail(hitList);
            emailService.sendEmail(emailConf, SUBJECT_OF_DAY_REPORT, emailContent, EMAIL_TYPE_HTML);
        }
    }

    // 构造周报邮件格式
    private String constructWeekReportEmail(List<Map<String, Object>> mapList) {
        String title = "<h2>用户慢查询统计</h2>";
        String table = "<table border='1'>ROWS</table>";
        String rows = "<tr><th>用户</th><th>慢查询总耗时(s)</th><th>慢查询次数</th><th>平均每次耗时(s)</th></tr>";
        String row = "<tr><td>user</td><td>total</td><td>queryTimes</td><td>avg</td></tr>";
        for (Map map : mapList) {
            rows = rows + row.replace("user", map.get("key").toString())
                    .replace("total", map.get("value").toString())
                    .replace("queryTimes", map.get("doc_count").toString())
                    .replace("avg", map.get("avg").toString());

        }
        table = table.replace("ROWS", rows);
        return title + table;
    }

    // 构造日报邮件格式
    private String constructDayReportEmail(List<Map> hitList) {
        String title = "<h2>用户慢查询日统计</h2>";
        String table = "<table border='1'>ROWS</table>";
        String rows = "<tr><th>时间</th><th>用户</th><th>耗时(s)</th><th>SQL</th></tr>";
        String row = "<tr><td>timestamp</td><td>user</td><td>spendTime</td><td>sql</td></tr>";

        for (Map map : hitList) {
            try {
                rows = rows + row.replace("timestamp", map.get("Time").toString().split("\\.")[0])
                        .replace("user", map.get("User").toString())
                        .replace("spendTime", map.get("Query_time").toString().split("\\.")[0])
                        .replace("sql", map.get("sql").toString());
            } catch (Exception e) {
                logger.warn("map key 获取失败，logstash 解析日志可能出错 {}", e);
            }
        }
        table = table.replace("ROWS", rows);
        return title + table;
    }
}
