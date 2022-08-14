package com.itheima.test;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;

import com.tanhua.server.AppServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class TestFastDFS {

    //从调度服务器获取，一个目标存储服务器，上传
    @Autowired
    private FastFileStorageClient client;

    @Autowired
    private FdfsWebServer fdfsWebServer;// 获取存储服务器的请求URL，即在yml中配置的web-server-url

    @Test
    public void testFileUpdate() throws FileNotFoundException {
 		//1.指定文件
        File file = new File("D:\\1.jpg");
        //2.文件上传,参数一：输入流，参数二：文件长度，参数三：文件后缀，参数四:文件其他信息
        StorePath storePath = client.uploadFile(new FileInputStream(file), file.length(), "jpg", null);
        //3.拼接访问地址 storePath+fdfsWebServer
        String path = fdfsWebServer.getWebServerUrl()+storePath.getFullPath();
        System.out.println(path);
    }
}