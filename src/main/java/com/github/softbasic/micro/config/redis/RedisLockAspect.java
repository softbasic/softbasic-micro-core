package com.github.softbasic.micro.config.redis;


import com.github.softbasic.micro.exception.BusinessException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static com.github.softbasic.micro.result.MicroStatus.REDIS_LOCK_WAIT_OVERTIME;

@Component
@Aspect
@Order(-1)
@ConditionalOnProperty(prefix="redisson",name = "enable", havingValue = "true")
public class RedisLockAspect {
    private Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    @Autowired
    private RedissonClient redissonClient;


    @Pointcut("@annotation(RedisLock)")
    private void pointcutMethod() {

    }


    @Around("pointcutMethod()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Class<?> targetClass = joinPoint.getTarget().getClass();
        Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getParameterTypes();
        Method objMethod = targetClass.getMethod(methodName, parameterTypes);
        RedisLock declaredAnnotation = objMethod.getDeclaredAnnotation(RedisLock.class);
        String lockName = declaredAnnotation.LockName();//锁的名字
        long leaseTime = declaredAnnotation.leaseTime();//过期时间
        TimeUnit unit = declaredAnnotation.unit();//单位
        long waitTime = declaredAnnotation.waitTime();//阻塞时间
        RLock lock = redissonClient.getFairLock("lock:" +lockName);//公平锁
        //尝试获取锁,没有获取到返回
        if (waitTime >= 0) {
            //时间内没有获取到锁，什么都不做
            if (lock.tryLock(waitTime, leaseTime, unit)) {
                return execute(joinPoint, lock);
            }
            throw new BusinessException(REDIS_LOCK_WAIT_OVERTIME);
        }
        //获取锁，一直等待
        if (waitTime < 0) {
            lock.lock(leaseTime, unit);
            return execute(joinPoint, lock);
        }
        return "";
    }

    private Object execute(ProceedingJoinPoint joinPoint, RLock lock) throws Throwable {
        logger.info("加锁"+lock.getName());
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            throw e;
        } finally {
            if (lock.isHeldByCurrentThread() && lock.isLocked()) {//当前线程是否持有锁
                logger.info("正常解锁"+lock.getName());
                lock.unlock();
            }else{
                logger.info("锁"+lock.getName()+"已提前释放");
            }
        }
    }



}
