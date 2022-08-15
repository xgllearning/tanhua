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
     *@Cacheable 注解表示这个方法有了缓存的功能，方法的返回值会被缓存下来
     * 下一次调用该方法前，会去检查是否缓存中已经有值，如果有就直接返回，不调用方法。如果没有，就调用方法，然后把结果缓存起来。
     *
     *
     */
    //@Cacheable(value="user" , key = "'test' + #id")
    //@Cacheable(value = "user")//加入注解支持,第一次从数据库中查询，查询完成后会把数据存放到缓存(名称就叫user)，第二次从缓存中获取
    @CachePut(value="user" , key = "'test' + #id")
    public User findById(Long id) {
        return userDao.findById(id);
    }


    //更新数据库时，删除redis中的缓存数据
//    @Caching(
//            evict = {
//                    @CacheEvict(value="user" , key = "'test' + #id"),
//                    @CacheEvict(value="user" , key = "#id")
//            }
//    )
@CacheEvict(value="user" , key = "'test' + #id")//执行完成后，清空指定的缓存，user-->user::test#id
    public void update(Long id) {
        userDao.update(id);
    }
}
