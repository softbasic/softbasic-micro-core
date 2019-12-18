package com.github.softbasic.micro.dao;

import org.springframework.beans.factory.annotation.Autowired;


public class BaseDao  {
    @Autowired(required=false)
    protected MongoDao mongoDao;
    @Autowired(required=false)
    protected RedisDao redisDao;
    @Autowired(required=false)
    protected RocketDao rocketDao;
}
