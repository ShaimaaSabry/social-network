package com.socialnetwork.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

@Configuration
public class UtilBeanConfig {

    @Bean
    JedisPool jedisPool() {
        return new JedisPool();
    }
}
