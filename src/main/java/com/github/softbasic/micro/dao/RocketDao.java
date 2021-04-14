package com.github.softbasic.micro.dao;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(prefix="rocketmq",name = "enable", havingValue = "true")
public class RocketDao {
    @Autowired(required=false)
    private RocketMQTemplate rocketMQTemplate;

    public RocketMQTemplate rocketMQTemplate() {
        return rocketMQTemplate;
    }
}
