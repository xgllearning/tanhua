package com.itheima.test;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 案例：
 *     将资料中的1.jpg上传到阿里云OSS
 *       存放的位置   /yyyy/MM/dd/xxxx.jpg
 */
public class OssTest {

    @Test
    public void testOss() throws FileNotFoundException {
        //1、配置图片路径
        String path = "C:\\study\\探花交友\\探花交友\\02-完善用户信息\\03-资料\\1.jpg";
        //2、构造FileInputStream
        FileInputStream inputStream = new FileInputStream(new File(path));
        //3、拼写图片路径,即object完整路径
        String filename = new SimpleDateFormat("yyyy/MM/dd").format(new Date())
                +"/"+ UUID.randomUUID().toString() + path.substring(path.lastIndexOf("."));

        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = "https://oss-cn-hangzhou.aliyuncs.com";
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = "yourAccessKeyId";
        String accessKeySecret = "yourAccessKeySecret";
        // 填写Bucket名称，例如examplebucket。
        String bucketName = "tanhua340";
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        String objectName = "exampledir/exampleobject.txt";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
//            // 填写Byte数组。
//            byte[] content = "Hello OSS".getBytes();
            // 创建PutObject请求。
//            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content));
            ossClient.putObject(bucketName, filename, inputStream);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
                String url = "https://tanhua340.oss-cn-beijing.aliyuncs.com/" + filename;
                System.out.println(url);
            }
        }
    }
}
