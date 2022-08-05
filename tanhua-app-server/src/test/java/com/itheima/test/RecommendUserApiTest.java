package com.itheima.test;

import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.server.AppServerApplication;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class RecommendUserApiTest {
    //引用服务-注入RecommendUserApi接口，暴露服务-暴露实现类
    @DubboReference
    private RecommendUserApi recommendUserApi;

    @Test
    public void testFindByMobile() {
        RecommendUser recommendUser = recommendUserApi.queryWithMaxScore(106l);
        System.out.println(recommendUser);
    }


}
