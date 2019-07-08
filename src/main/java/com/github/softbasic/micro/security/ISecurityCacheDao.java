package com.github.softbasic.micro.security;

import com.alibaba.fastjson.JSONObject;

public interface ISecurityCacheDao {


    JSONObject getUserInfo(String key);


    String getAuth(String key);

    JSONObject setUserInfo(String key, JSONObject userInfo);

    void removeUserInfo(String key);

    JSONObject getVersion(String key);

}
