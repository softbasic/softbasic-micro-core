package com.github.softbasic.micro.config.redis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2019/4/16.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisErrorRemoveParam {

    String[] cacheNames() default {}; //缓存名字

    int[] keys() default {}; //key可以指定多个
}
