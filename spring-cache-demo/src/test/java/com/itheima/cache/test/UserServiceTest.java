package com.itheima.cache.test;

import com.itheima.cache.domain.User;
import com.itheima.cache.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;


    /**
     * 根据id查询用户
     */
    @Test
    public void testFindById() {
        for (int i = 0; i < 5; i++) {
            User user = userService.findById(1l);
            System.out.println(user);
        }

//        User user = userService.findById(2l);
//        System.out.println(user);
    }

    //更新：更新数据库，删除redis中的缓存数据，通过加注解实现
    @Test
    public void testUpdate() {
        userService.update(1l);
    }
}
