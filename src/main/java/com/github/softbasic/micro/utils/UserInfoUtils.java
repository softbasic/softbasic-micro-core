package com.github.softbasic.micro.utils;

import com.alibaba.fastjson.JSONObject;
import com.github.softbasic.micro.config.MicroContext;
import com.github.softbasic.micro.exception.BusinessException;
import com.github.softbasic.micro.log.MicroLogger;
import com.github.softbasic.micro.log.MicroLoggerFactory;
import com.github.softbasic.micro.result.MicroStatus;
import com.github.softbasic.micro.security.ISecurityCacheDao;
import com.github.softbasic.micro.security.SecurityCacheDao;
import org.redisson.codec.JsonJacksonCodec;

public class UserInfoUtils {

    private static final MicroLogger log = MicroLoggerFactory.getLogger(UserInfoUtils.class);

    private UserInfoUtils() {
    }

    private static volatile UserInfoUtils instance = null;

    private static ISecurityCacheDao securityCacheDao = null;

    public static UserInfoUtils getInstance() {
        if (instance == null) {
            synchronized (UserInfoUtils.class) {
                if (instance == null) {
                    securityCacheDao = (SecurityCacheDao) SpringContextUtils.getBean("securityCacheDao");
                    instance = new UserInfoUtils();
                    return instance;
                }
            }
        }
        return instance;
    }

    public JSONObject getUserInfo() {
        JSONObject userInfo = securityCacheDao.getUserInfo(MicroContext.getToken());
        if (userInfo == null) {
            log.error("登陆超时！");
            throw new BusinessException(MicroStatus.LANDING_TIMEOUT);
        }
        if (BaseUtils.isBlank(userInfo.getString("id"))) {
            log.error("登陆信息异常，请重新登陆！");
            throw new BusinessException(MicroStatus.USER_ID_IS_NULL);
        }
        return userInfo;
    }
}
