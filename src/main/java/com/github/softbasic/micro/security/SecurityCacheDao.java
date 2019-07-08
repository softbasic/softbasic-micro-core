package com.github.softbasic.micro.security;

import com.alibaba.fastjson.JSONObject;
import com.github.softbasic.micro.dao.BaseDao;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;


@Repository
public class SecurityCacheDao extends BaseDao implements ISecurityCacheDao {


    @Cacheable(key = "#key", cacheNames = "secure:userInfo")
    public JSONObject getUserInfo(String key) {
        return null;
    }


    @Cacheable(key = "#key", cacheNames = "secure:interfaceAuth")
    public String getAuth(String key) {
        return null;
    }


    @CachePut(key = "#key", cacheNames = "secure:userInfo")
    public JSONObject setUserInfo(String key, JSONObject userInfo) {
        return userInfo;
    }

    @CacheEvict(key = "#key", cacheNames = "secure:userInfo")
    public void removeUserInfo(String key) {
    }

    @Cacheable(key = "#key", cacheNames = "secure:interfaceVersion")
    public JSONObject getVersion(String key) {
        return null;
    }
}
