package com.github.softbasic.micro.security;

import com.github.softbasic.micro.dao.BaseDao;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Repository;

@CacheConfig(cacheNames="example.user")
@Repository
public class SecurityCacheDao extends BaseDao implements ISecurityDao {

}
