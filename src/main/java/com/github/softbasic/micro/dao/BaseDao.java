package com.github.softbasic.micro.dao;

import org.springframework.beans.factory.annotation.Autowired;


public class BaseDao  {
    @Autowired
    protected MongoDao mongoDao;
    @Autowired
    protected RedisDao redisDao;
    @Autowired
    protected RocketDao rocketDao;
}
