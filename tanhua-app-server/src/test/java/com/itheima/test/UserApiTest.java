package com.itheima.test;


import com.tanhua.dubbo.api.UserApi;
import com.tanhua.model.domain.User;
import com.tanhua.server.AppServerApplication;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class UserApiTest {
    //引用服务-注入UserApi接口，暴露服务-暴露实现类
    @DubboReference
    private UserApi userApi;

    @Test
    public void testFindByMobile() {
        User user = userApi.findByMobile("13800138000");
        System.out.println(user);
    }
}
