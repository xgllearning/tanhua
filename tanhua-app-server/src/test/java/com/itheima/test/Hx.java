package com.itheima.test;

import com.easemob.im.server.EMProperties;
import com.easemob.im.server.EMService;
import org.junit.Before;
import org.junit.Test;

public class Hx {

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
