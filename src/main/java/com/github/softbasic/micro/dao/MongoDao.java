package com.github.softbasic.micro.dao;

import com.github.softbasic.micro.model.BaseDto;
import com.github.softbasic.micro.model.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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
