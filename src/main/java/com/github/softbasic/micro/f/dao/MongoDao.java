package com.github.softbasic.micro.f.dao;

import com.github.softbasic.micro.g.model.BaseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MongoDao {
    @Autowired
    private MongoTemplate mongoSecondary;
    @Autowired
    private MongoTemplate mongoPrimary;

    public void test(){

        BaseDTO d=mongoPrimary.save(new BaseDTO()) ;}
}