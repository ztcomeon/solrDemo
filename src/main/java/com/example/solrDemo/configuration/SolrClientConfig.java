package com.example.solrDemo.configuration;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: SolrClientConfig
 * @Description:初始化solr连接
 */
//@Configuration
public class SolrClientConfig {

    /**
     * 连接超时时间Timeout in milliseconds.
     **/
    public static final int CONNECTION_TIME_OUT = 500000;

    /**
     * read timeout.
     **/
    public static final int SO_TIME_OUT = 50000;

    /**
     * solr host.
     **/
    @Value("${solr.host}")
    private String HTTP_SOLR_CLIENT;

    /**
     * solr coreName.
     **/
    @Value("${solr.coreName}")
    private String CORE_NAME;

    @Bean
    public SolrClient connetHttpSolrClientServer() {
        HttpSolrClient server = new HttpSolrClient.Builder(HTTP_SOLR_CLIENT + CORE_NAME)
                .withConnectionTimeout(CONNECTION_TIME_OUT)
                .withSocketTimeout(SO_TIME_OUT)
                .build();
//        server.setParser(new XMLResponseParser());
//        server.setFollowRedirects(false);
        return server;
    }
}
