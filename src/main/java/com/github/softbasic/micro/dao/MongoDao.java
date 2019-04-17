package com.github.softbasic.micro.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MongoDao {
    @Autowired
    private MongoTemplate mongoSecondary;
    @Autowired
    private MongoTemplate mongoPrimary;

}
