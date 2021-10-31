package com.newcoder.community.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

/**
 * @author chenmin
 */
@Configuration
public class EsConfig {
    @Value("${elasticsearch.url}")
    private String esUrl;


    @Bean
    RestHighLevelClient client() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                //elasticsearch地址:localhost:9200 写在配置文件中就可以了
                .connectedTo(esUrl)
                .build();

        return RestClients.create(clientConfiguration).rest();
    }
}
