package com.dashu.loganalysis.report.serviceImp;

import com.dashu.loganalysis.report.dao.ZipkinRepository;
import com.dashu.loganalysis.report.service.EmailService;
import com.dashu.loganalysis.report.service.ZipkinReportService;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @Description ZipkinReportService 实现类
 * @Author: xuyouchang
 * @Date 2019/7/23 下午1:43
 **/
@Service
public class ZipkinReportServiceImp implements ZipkinReportService {
    private static final Logger logger = LoggerFactory.getLogger(ZipkinReportServiceImp.class);

    private static final String INDEX_OF_ZIPKIN = "zipkin*";
    private static final String FIELD_OF_SERVICE = "localEndpoint.serviceName";
    private static final String FIELD_OF_REQUEST = "name";
    private static final String FIELD_OF_DURATION = "duration";
    private static final String SUBJECT_OF_WEEK_REPORT = "zipkin 周报";

    @Resource
    private ZipkinRepository zipkinRepository;
    @Resource
    private EmailService emailService;

    @Override
    public void zipkinWeekReport(Map emailConf) {
        SearchHit[] searchHits = zipkinRepository.getTop(INDEX_OF_ZIPKIN, FIELD_OF_DURATION, 10);
        List<Map<String, Object>> countService = zipkinRepository.getCountByField(INDEX_OF_ZIPKIN, FIELD_OF_SERVICE,100);
        List<Map<String, Object>> countRequest = zipkinRepository.getCountByField(INDEX_OF_ZIPKIN, FIELD_OF_REQUEST,10);

        String emailContent = makeTopContent(searchHits) + makeCountContent("请求次数统计 Top 10", countRequest)
                + makeCountContent("服务调用次数统计", countService);
        emailService.sendEmail(emailConf, SUBJECT_OF_WEEK_REPORT, emailContent, "html");

    }

    /* 构建 count 邮件内容 */
    private String makeCountContent(String titlename, List<Map<String, Object>> mapList) {
        String title = "<h2>TITLE</h2>";
        String table = "<table border='1'>ROWS</table>";
        String rows = "<tr><th>名称</th><th>调用次数</th></tr>";
        String row = "<tr><td>name</td><td>times</td></tr>";

        for (Map map : mapList) {
            rows = rows + row.replace("name", map.get("key").toString())
                    .replace("times", map.get("value").toString());
        }
        title = title.replace("TITLE", titlename);
        table = table.replace("ROWS", rows);
        return title + table;
    }

    /* 构造 Top duration 邮件内容 */
    private String makeTopContent(SearchHit[] searchHits) {
        String title = "<h2>请求调用时长 Top 10</h2>";
        String table = "<table border='1'>ROWS</table>";
        String rows = "<tr><th>ID</th><th>调用时长(s)</th><th>服务名</th><th>请求</th><th>类型</th><th>时间</th><th>错误信息</th></tr>";
        String row = "<tr><td>id</td><td>duration</td><td>servicename</td><td>request</td><td>kind</td><td>time</td><td>error</td></tr>";
        for (SearchHit hit : searchHits) {
            try{
                String error = "";
                Map sourceMap = hit.getSourceAsMap();
                if (sourceMap.containsKey("tags")) {
                    if (((Map)sourceMap.get("tags")).containsKey("error")) {
                        error = ((Map)sourceMap.get("tags")).get("error").toString();
                    }
                }
                Integer duration = Integer.valueOf(sourceMap.get("duration").toString());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date= new Date(Long.valueOf(sourceMap.get("timestamp_millis").toString()));
                rows = rows + row.replace("id",sourceMap.get("id").toString())
                        .replace("duration", String.valueOf(duration/1000000))
                        .replace("servicename", ((Map)sourceMap.get("localEndpoint")).get("serviceName").toString())
                        .replace("request", sourceMap.get("name").toString())
                        .replace("kind", sourceMap.get("kind").toString())
                        .replace("time", sdf.format(date))
                        .replace("error", error);
            }catch (Exception e) {
                logger.warn("email 内容填充异常 {}", e);
            }
        }
        table = table.replace("ROWS", rows);
        return title + table;
    }


}
