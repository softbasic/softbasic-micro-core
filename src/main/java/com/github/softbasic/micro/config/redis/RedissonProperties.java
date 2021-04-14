package com.github.softbasic.micro.config.redis;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "classpath:redisson.properties")
@ConfigurationProperties(prefix = "redisson")
@ConditionalOnProperty(prefix="redisson",name = "enable", havingValue = "true")
@Data
public class RedissonProperties {
    private int timeout;

    private int connectTimeout;

    private String address;

    private String master;

    private String slaves;

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
