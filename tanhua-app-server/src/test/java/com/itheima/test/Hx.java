package com.itheima.test;

import com.easemob.im.server.EMProperties;
import com.easemob.im.server.EMService;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.model.domain.User;
import com.tanhua.server.AppServerApplication;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class Hx {

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @DubboReference
    private UserApi userApi;

    private EMService service;
    @Before
    public void init(){
        EMProperties properties = EMProperties.builder()
                .setAppkey("")
                .setClientId("")
                .setClientSecret("")
                .build();
        service = new EMService(properties);
    }

    @Test
    public void test(){
        service.user().create("user001","123456").block();
    }


}
