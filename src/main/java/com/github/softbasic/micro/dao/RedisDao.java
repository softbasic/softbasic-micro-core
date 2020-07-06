package com.github.softbasic.micro.dao;

import org.redisson.api.*;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Repository
@ConditionalOnProperty(prefix="redisson",name = "enable", havingValue = "true")
public class RedisDao {
    @Autowired(required=false)
    private RedissonClient redissonClient;

    public RedissonClient redissonClient(){
        return redissonClient;
    }

    /**`
     * 获取字符串对象
     *
     * @param objectName
     * @return
     */
    public <T> RBucket<T> getRBucket(String objectName) {
        RBucket<T> bucket = redissonClient.getBucket(objectName);
        return bucket;
    }

    /**
     * 获取Map对象
     *
     * @param objectName
     * @return
     */
    public <K, V> RMap<K, V> getRMap(String objectName) {
        RMap<K, V> map = redissonClient.getMap(objectName);
        return map;
    }

    /**
     * 获取有序集合
     *
     * @param objectName
     * @return
     */
    public <V> RSortedSet<V> getRSortedSet(String objectName) {
        RSortedSet<V> sortedSet = redissonClient.getSortedSet(objectName);
        return sortedSet;
    }

    /**
     * 获取集合
     *
     * @param objectName
     * @return
     */
    public <V> RSet<V> getRSet(String objectName) {
        RSet<V> rSet = redissonClient.getSet(objectName);
        return rSet;
    }

    /**
     * 获取列表
     *
     * @param objectName
     * @return
     */
    public <V> RList<V> getRList(String objectName) {
        RList<V> rList = redissonClient.getList(objectName);
        return rList;
    }

    /**
     * 获取队列
     *
     * @param objectName
     * @return
     */
    public <V> RQueue<V> getRQueue(String objectName) {
        RQueue<V> rQueue = redissonClient.getQueue(objectName);
        return rQueue;
    }

    /**
     * 获取双端队列
     *
     * @param objectName
     * @return
     */
    public <V> RDeque<V> getRDeque(String objectName) {
        RDeque<V> rDeque = redissonClient.getDeque(objectName);
        return rDeque;
    }


    /**
     * 获取锁
     *
     * @param objectName
     * @return
     */
    public RLock getRLock(String objectName) {
        RLock rLock = redissonClient.getLock(objectName);
        return rLock;
    }

    /**
     * 获取读取锁
     *
     * @param objectName
     * @return
     */
    public RReadWriteLock getRWLock(String objectName) {
        RReadWriteLock rwlock = redissonClient.getReadWriteLock(objectName);
        return rwlock;
    }

    /**
     * 获取原子数
     *
     * @param objectName
     * @return
     */
    public RAtomicLong getRAtomicLong(String objectName) {
        RAtomicLong rAtomicLong = redissonClient.getAtomicLong(objectName);
        return rAtomicLong;
    }

    /**
     * 获取记数锁
     *
     * @param objectName
     * @return
     */
    public RCountDownLatch getRCountDownLatch(String objectName) {
        RCountDownLatch rCountDownLatch = redissonClient.getCountDownLatch(objectName);
        return rCountDownLatch;
    }

    /**
     * 删除缓存
     * @param cacheName
     * @param keys
     * @return
     */
    public long removeCaches(String cacheName,Object... keys){
        RMapCache<Object, Object> mapCache = redissonClient.getMapCache(cacheName);
        return mapCache.fastRemove(keys);
    }


    public RLock lock(String name) {
        RLock fairLock = redissonClient.getFairLock("lock:" + name);
        fairLock.lock(5 * 60 * 1000, TimeUnit.MILLISECONDS);
        return fairLock;
    }

    public RLock lock(String name, long leaseTime, TimeUnit unit) {
        RLock fairLock = redissonClient.getFairLock("lock:" + name);
        fairLock.lock(leaseTime, unit);
        return fairLock;
    }

    public RLock tryLock(String name) throws InterruptedException {
        RLock fairLock = redissonClient.getFairLock("lock:" + name);
        fairLock.tryLock(5 * 60 * 1000, TimeUnit.MILLISECONDS);
        return fairLock;
    }

    public RLock tryLock(String name, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
        RLock fairLock = redissonClient.getFairLock("lock:" + name);
        fairLock.tryLock(waitTime, leaseTime, unit);
        return fairLock;
    }

    public void unLock(RLock lock) {
        if (lock.isHeldByCurrentThread() && lock.isLocked()) {//当前线程是否持有锁
            lock.unlock();
        }
    }

}
