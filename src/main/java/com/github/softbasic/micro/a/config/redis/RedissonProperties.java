package com.github.softbasic.micro.a.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "classpath:redisson.properties")
@ConfigurationProperties(prefix = "redisson")
@Data
public class RedissonProperties {
    private int timeout;

    private int connectTimeout;

    private String address;

    private String password;

    private int connectionPoolSize;

    private int connectionMinimumIdleSize;

    private int retryAttempts;

    private int retryInterval;

    private int redissonCacheDefault;

    private final RedissonCache redissonCache = new RedissonCache();
    /**
     * 缓存配置
     */
    @Data
    public static class RedissonCache{
        private String cacheConfigs;
    }

}
