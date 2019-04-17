package com.github.softbasic.micro.config.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedissonConfig {

    @Bean(destroyMethod = "shutdown")
    RedissonClient redissonClient(RedissonProperties redssionProperties) {
        Config config = new Config().setCodec(new JsonJacksonCodec());
        SingleServerConfig singleServerConfig = config.useSingleServer()
                .setAddress(redssionProperties.getAddress())//连接地址
                .setConnectTimeout(redssionProperties.getConnectTimeout())//连接超时
                .setTimeout(redssionProperties.getTimeout())//Redis服务器响应超时
                .setConnectionPoolSize(redssionProperties.getConnectionPoolSize())//Redis连接的最大池大小
                .setConnectionMinimumIdleSize(redssionProperties.getConnectionMinimumIdleSize())//最小空闲Redis连接量
                .setRetryAttempts(redssionProperties.getRetryAttempts())//命令失败重试次数
                .setRetryInterval(redssionProperties.getRetryInterval());//命令重试发送时间间隔
        if (!StringUtils.isEmpty(redssionProperties.getPassword())) {
            singleServerConfig.setPassword(redssionProperties.getPassword());
        }
        return Redisson.create(config);
    }

    @Primary
    @Bean
    CacheManager redissonCacheManager(RedissonClient redissonClient, RedissonProperties redssionProperties) {
        RedissonProperties.RedissonCache redissonCache = redssionProperties.getRedissonCache();
        String cacheConfigs = redissonCache.getCacheConfigs()==null?"":redissonCache.getCacheConfigs();
        Map<String, CacheConfig> configMap = new HashMap<>();
        String[] cacheConfigsArray = cacheConfigs.split(",");
        Arrays.stream(cacheConfigsArray).forEach(cacheConfig -> {
            String[] params = cacheConfig.split("&");
            String cacheName = params[0];//缓存名字
            long ttl = Long.valueOf(params[1]);//过期时间
            long maxIdleTime = Long.valueOf(params[2]);//最大空闲时间
            CacheConfig config = new CacheConfig(ttl, maxIdleTime);
            configMap.put(cacheName, config);
        });
        return new RedissonSpringCacheManager(redissonClient, configMap, new JsonJacksonCodec());
    }

}
