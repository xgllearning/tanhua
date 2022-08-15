package com.itheima.cache.dao;

import com.itheima.cache.domain.User;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class UserDao {

    public User findById(Long id){
        System.out.println("查询数据库");
        try {
            Thread.sleep(2000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new User(id,"张三");
    }

    public List<User> findAll() {
        List<User> list = Collections.EMPTY_LIST;
        list.add(new User(1l,"张三"));
        list.add(new User(2l,"李四"));
        list.add(new User(3l,"王五"));
        return list;
    }

    public void update(Long id) {
        System.out.println("根据id更新");
    }
}
