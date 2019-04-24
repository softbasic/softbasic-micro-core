package com.github.softbasic.micro.dao;

import com.github.softbasic.micro.model.BaseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MongoDao{
    @Autowired
    private MongoTemplate mongoSecondary;
    @Autowired
    private MongoTemplate mongoPrimary;

    public MongoTemplate primary() {
        return mongoPrimary;
    }

    public MongoTemplate secondary() {
        return mongoSecondary;
    }
}
