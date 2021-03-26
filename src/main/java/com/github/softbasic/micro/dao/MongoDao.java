package com.github.softbasic.micro.dao;

import com.github.softbasic.micro.model.BaseDto;
import com.github.softbasic.micro.model.PageVO;
import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@ConditionalOnProperty(prefix="spring.data.mongodb",name = "enable", havingValue = "true")
public class MongoDao{
    @Autowired(required=false)
    private MongoTemplate mongoSecondary;
    @Autowired(required=false)
    private MongoTemplate mongoPrimary;

    @Autowired
    private MongoClient mongoClientPrimary;
    @Autowired
    private MongoClient mongoClientSecondary;

    public MongoTemplate primary() {
        return mongoPrimary.withSession(mongoClientPrimary.startSession());
    }

    /**
     * 子系统中很多地方写法不规范，读写分离导致的延迟数据有问题，暂时全部改为主节点
     * 这里 mongoPrimary和mongoClientPrimary要对应，负责查询报：state should be: ClientSession from same MongoClient 错误
     * @return
     */
    public MongoTemplate secondary() {
        return mongoPrimary.withSession(mongoClientPrimary.startSession());
    }

    /**
     * 副节点分页查询,表名取自注解或者类名
     * @param resultClass 返回值封装类
     * @param query 查询条件
     * @param pageNo 第几页
     * @param pageSize 每页的条数
     * @param <T> 泛型参数
     * @return
     */
    public <T> PageVO<T> page(Class<T> resultClass, Query query, int pageNo, int pageSize) {
        String collectionName=this.getCollectionName(resultClass);
        int total = (int) this.mongoSecondary.count(query, collectionName);
        if (total < 1) {
            return new PageVO();
        } else {
            if (pageNo < 1) {
                pageNo = 1;
            }

            if (pageSize < 1) {
                pageSize = total;
            }

            query.limit(pageSize).skip((pageNo - 1) * pageSize);
            int startIndex = PageVO.getStartOfPage(pageNo, pageSize);
            List result = this.mongoSecondary.find(query, resultClass, collectionName);
            return new PageVO(startIndex, total, pageSize, result);
        }
    }

    /**
     * 副节点分页查询
     * @param resultClass
     * @param query
     * @param pageNo
     * @param pageSize
     * @param collectionName
     * @param <T>
     * @return
     */
    public <T> PageVO<T> page(Class<T> resultClass, Query query, int pageNo, int pageSize, String collectionName) {
        int total = (int) this.mongoSecondary.count(query, collectionName);
        if (total < 1) {
            return new PageVO();
        } else {
            if (pageNo < 1) {
                pageNo = 1;
            }

            if (pageSize < 1) {
                pageSize = total;
            }

            query.limit(pageSize).skip((pageNo - 1) * pageSize);
            int startIndex = PageVO.getStartOfPage(pageNo, pageSize);
            List result = this.mongoSecondary.find(query, resultClass, collectionName);
            return new PageVO(startIndex, total, pageSize, result);
        }
    }

    /**
     * 副节点聚合分页查询,表名取自注解或者类名
     * @param resultClass 返回值封装类
     * @param operations 聚合函数查询参数
     * @param pageNo 第几页
     * @param pageSize 每页的条数
     * @param <T> 泛型参数
     * @return
     */
    public <T> PageVO<T> page(Class<T> resultClass,List<AggregationOperation> operations,int pageNo, int pageSize) {
        String collectionName=this.getCollectionName(resultClass);
        Aggregation aggregation = Aggregation.newAggregation(operations);
        int total = (int) this.mongoSecondary.aggregate(aggregation, collectionName, resultClass).getMappedResults().size();

        if (total < 1) {
            return new PageVO();
        } else {
            if (pageNo < 1) {
                pageNo = 1;
            }

            if (pageSize < 1) {
                pageSize = total;
            }
            operations.add(Aggregation.skip((pageNo - 1) * pageSize));
            operations.add(Aggregation.limit(pageSize));
            aggregation = Aggregation.newAggregation(operations);
            int startIndex = PageVO.getStartOfPage(pageNo, pageSize);
            List result = this.mongoSecondary.aggregate(aggregation, collectionName, resultClass).getMappedResults();
            return new PageVO(startIndex, total, pageSize, result);
        }
    }


    /**
     * 副节点聚合分页查询
     * @param resultClass
     * @param operations
     * @param pageNo
     * @param pageSize
     * @param collectionName
     * @param <T>
     * @return
     */
    public <T> PageVO<T> page(Class<T> resultClass,List<AggregationOperation> operations, int pageNo, int pageSize, String collectionName) {
        Aggregation aggregation = Aggregation.newAggregation(operations);

        int total = (int) this.mongoSecondary.aggregate(aggregation, collectionName, resultClass).getMappedResults().size();
        if (total < 1) {
            return new PageVO();
        } else {
            if (pageNo < 1) {
                pageNo = 1;
            }

            if (pageSize < 1) {
                pageSize = total;
            }
            operations.add(Aggregation.skip((pageNo - 1) * pageSize));
            operations.add(Aggregation.limit(pageSize));
            aggregation = Aggregation.newAggregation(operations);
            int startIndex = PageVO.getStartOfPage(pageNo, pageSize);
            List result = this.mongoSecondary.aggregate(aggregation, collectionName, resultClass).getMappedResults();
            return new PageVO(startIndex, total, pageSize, result);
        }
    }


    /**
     * 主节点分页查询,表名取自注解或者类名
     * @param resultClass 返回值封装类
     * @param query 查询条件
     * @param pageNo 第几页
     * @param pageSize 每页的条数
     * @param <T> 泛型参数
     * @return
     */
    public <T> PageVO<T> pagePrimary(Class<T> resultClass, Query query, int pageNo, int pageSize) {
        String collectionName=this.getCollectionName(resultClass);
        int total = (int) this.mongoPrimary.count(query, collectionName);
        if (total < 1) {
            return new PageVO();
        } else {
            if (pageNo < 1) {
                pageNo = 1;
            }

            if (pageSize < 1) {
                pageSize = total;
            }

            query.limit(pageSize).skip((pageNo - 1) * pageSize);
            int startIndex = PageVO.getStartOfPage(pageNo, pageSize);
            List result = this.mongoPrimary.find(query, resultClass, collectionName);
            return new PageVO(startIndex, total, pageSize, result);
        }
    }

    /**
     * 主节点分页查询
     * @param resultClass
     * @param query
     * @param pageNo
     * @param pageSize
     * @param collectionName
     * @param <T>
     * @return
     */
    public <T> PageVO<T> pagePrimary(Class<T> resultClass, Query query, int pageNo, int pageSize, String collectionName) {
        int total = (int) this.mongoPrimary.count(query, collectionName);
        if (total < 1) {
            return new PageVO();
        } else {
            if (pageNo < 1) {
                pageNo = 1;
            }

            if (pageSize < 1) {
                pageSize = total;
            }

            query.limit(pageSize).skip((pageNo - 1) * pageSize);
            int startIndex = PageVO.getStartOfPage(pageNo, pageSize);
            List result = this.mongoPrimary.find(query, resultClass, collectionName);
            return new PageVO(startIndex, total, pageSize, result);
        }
    }

    /**
     * 取类的注解值作为表名
     * @param resultClass
     * @return
     */
    public String getCollectionName(Class<?> resultClass) {
        //获取类名
        String collection = resultClass.getSimpleName();
        //获取实体类上的注解
        Document serverModel = (Document) resultClass.getAnnotation(Document.class);
        //如果有Document注解，并且设置了collection字段，则取它作为表名
        if (serverModel != null) {
            collection = serverModel.collection();
        }

        return collection;
    }
}
