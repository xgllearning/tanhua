package com.itheima.test;


import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.User;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.server.AppServerApplication;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class UserInfoApiTest {
    //引用服务-注入UserApi接口，暴露服务-暴露实现类
    @DubboReference
    private UserInfoApi userInfoApi;

    @Test
    public void testFindByMobile() {
        ArrayList<Long> list = new ArrayList<>();
        list.add(1l);
        list.add(21l);
        list.add(3l);
        list.add(4l);
        list.add(5l);
        Map<Long, UserInfo> map = userInfoApi.findByIds(list, null);
        map.forEach((k,v)-> System.out.println(k+"------"+v));
    }
}
