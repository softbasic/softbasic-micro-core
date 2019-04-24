package com.github.softbasic.micro.config.redis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2019/4/12.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLock {
    String LockName(); //锁的名字

    long leaseTime() default 5*60*1000; //锁的过期时间

    long waitTime() default -1; //等待时间,默认不等待

    TimeUnit unit() default TimeUnit.MILLISECONDS; //时间单位，默认毫秒
}
