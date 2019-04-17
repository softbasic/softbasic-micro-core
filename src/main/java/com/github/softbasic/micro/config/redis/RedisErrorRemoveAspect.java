package com.github.softbasic.micro.config.redis;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Component
@Aspect
@Order(-1)
public class RedisErrorRemoveAspect {

    @Autowired
    private RedissonClient redissonClient;


    @Pointcut("@annotation(com.github.softbasic.micro.config.redis.RedisErrorRemove)")
    private void pointcutMethod() {

    }

    @AfterThrowing(value = "pointcutMethod()")
    public void afterThrowing(JoinPoint joinPoint) throws Throwable {
        System.out.println("异常通知。。。清除缓存");
        String methodName = joinPoint.getSignature().getName();
        Class<?> targetClass = joinPoint.getTarget().getClass();
        Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getParameterTypes();
        Method objMethod = targetClass.getMethod(methodName, parameterTypes);
        RedisErrorRemove declaredAnnotation = objMethod.getDeclaredAnnotation(RedisErrorRemove.class);
        RedisErrorRemoveParam[] cache = declaredAnnotation.cache();
        Arrays.stream(cache).forEach(config -> {
            String[] cacheNames = config.cacheNames();
            int[] keys = config.keys();
            Arrays.stream(cacheNames).forEach(cacheName -> {
                RMapCache<Object, Object> mapCache = redissonClient.getMapCache(cacheName);
                Arrays.stream(keys).forEach(mapCache::fastRemove);
            });
        });
    }
}
