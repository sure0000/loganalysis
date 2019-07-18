package com.dashu.loganalysis.report.dao;


import com.dashu.loganalysis.Util.ReadConf;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

/**
 * @Description es 连接
 * @Author: xuyouchang
 * @Date 2019/7/9 下午7:46
 **/
public enum EsInstance {
    INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(EsInstance.class);

    /**
     * ES 连接
     * @return connect
     */
    @SuppressWarnings(value = "unchecked")
    public RestHighLevelClient connect() {
        ReadConf readConf = new ReadConf("conf/es.yml");
        Map<String,Map> esConf = readConf.readYml();
        Map connect = esConf.get("connect");
        String username = connect.get("username").toString();
        String password = connect.get("password").toString();
        String host = connect.get("host").toString();
        Integer port = Integer.valueOf(connect.get("port").toString());
        String scheme = connect.get("scheme").toString();

        logger.debug("ES configure is {}", esConf);

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        return new RestHighLevelClient(RestClient.builder(new HttpHost(host, port,scheme))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                }).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback(){
                    @Override
                    public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                        return requestConfigBuilder.setConnectTimeout(500000)
                                .setSocketTimeout(600000);
                    }
                }).setMaxRetryTimeoutMillis(600000));
    }
}
