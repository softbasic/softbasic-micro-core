package com.github.softbasic.micro.config;

import com.github.softbasic.micro.exception.BusinessException;
import com.github.softbasic.micro.utils.BaseUtils;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.github.softbasic.micro.result.MicroStatus.UNAUTHORIZED;

/**
 * 全局变量
 */
public final class MicroContext {

    //存储token
    private static final ThreadLocal<String> token = new ThreadLocal();


    /**
     * 设置token
     * @param token
     */
    public static void setToken(String token){
        if(BaseUtils.isBlank(token))
            throw new BusinessException(UNAUTHORIZED);
        MicroContext.token.set(token);
    }

    /**
     * 获取token
     * @return
     */
    public static String getToken(){
        String token=MicroContext.token.get();
        if(BaseUtils.isBlank(token))
            throw new BusinessException(UNAUTHORIZED);
        return MicroContext.token.get();
    }



}