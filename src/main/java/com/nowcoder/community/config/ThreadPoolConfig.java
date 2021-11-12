package com.nowcoder.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author chenmin
 * @date 2021/11/12
 */
@Configuration
@EnableScheduling
@EnableAsync
public class ThreadPoolConfig {

}
