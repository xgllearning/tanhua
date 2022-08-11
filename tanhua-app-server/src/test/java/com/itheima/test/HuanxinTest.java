package com.itheima.test;


import com.easemob.im.server.EMException;

import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.server.AppServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class HuanxinTest {
    @Autowired
    private HuanXinTemplate huanXinTemplate;


    @Test
    public void test(){
       huanXinTemplate.createUser("user002","123456");
    }
}
