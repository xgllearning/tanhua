package com.itheima.cache.service;

import com.itheima.cache.dao.UserDao;
import com.itheima.cache.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    /**
     * value：名称空间（分组）
     * key: 支持springel
     * redis-key的命名规则：
     *      value + "::" + key
     */
    //@Cacheable(value="user" , key = "'test' + #id")
    //@CachePut(value="user" , key = "'test' + #id")
    @Cacheable(value = "user")//加入注解支持,第一次从数据库中查询，查询完成后会把数据存放到缓存(名称就叫user)，第二次从缓存中获取
    public User findById(Long id) {
        return userDao.findById(id);
    }

    //@CacheEvict(value="user" , key = "'test' + #id")
//    @Caching(
//            evict = {
//                    @CacheEvict(value="user" , key = "'test' + #id"),
//                    @CacheEvict(value="user" , key = "#id")
//            }
//    )
    public void update(Long id) {
        userDao.update(id);
    }
}
