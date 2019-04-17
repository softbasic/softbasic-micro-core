package com.github.softbasic.micro.dao;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RocketDao {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

}
