package com.tanhua.dubbo;


import com.tanhua.dubbo.utils.IdWorker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IdWorkerTest {


    @Autowired
    private IdWorker idWorker;

    @Test
    public void idWorkerTest(){
        //生成的movement表自增Id
        Long id = idWorker.getNextId("movement");
        //生成的test1表自增Id,如果不存在则会自动创建
        Long id1 = idWorker.getNextId("test1");

    }
}
