package com.newcoder.community.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

/**
 * @author chenmin
 */
@Configuration
public class EsConfig extends AbstractElasticsearchConfiguration {
    @Value("${elasticsearch.url}")
    private String esUrl;

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                //elasticsearch地址:localhost:9200 写在配置文件中就可以了
                .connectedTo(esUrl)
                .build();

        return RestClients.create(clientConfiguration).rest();
    }
}
