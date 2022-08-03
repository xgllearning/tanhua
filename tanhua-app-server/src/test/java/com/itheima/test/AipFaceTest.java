package com.itheima.test;

import com.baidu.aip.face.AipFace;
import com.tanhua.autoconfig.template.AipFaceTemplate;
import com.tanhua.server.AppServerApplication;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class AipFaceTest {
    @Autowired
    public AipFaceTemplate aipFaceTemplate;

    @Test
    public void detect(){
        boolean detect = aipFaceTemplate.detect("https://tanhua001.oss-cn-beijing.aliyuncs.com/2021/04/19/a3824a45-70e3-4655-8106-a1e1be009a5e.jpg");
        System.out.println(detect);
    }


    //设置APPID/AK/SK
    public static final String APP_ID = "";
    public static final String API_KEY = "";
    public static final String SECRET_KEY = "";

    public static void main(String[] args) {
        // 初始化一个AipFace
        AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);


        // 图片先上传到oss中，然后aip人脸识别接口
        String image = "https://tanhua001.oss-cn-beijing.aliyuncs.com/2021/04/19/a3824a45-70e3-4655-8106-a1e1be009a5e.jpg";
        //String image = "https://tanhua001.oss-cn-beijing.aliyuncs.com//2022/08/03/cd27bc40-4613-4f51-968c-cb006f5cbb3b.jpg";
        String imageType = "URL";

        HashMap<String, String> options = new HashMap<String, String>();
        options.put("face_field", "age");
        options.put("max_face_num", "2");
        options.put("face_type", "LIVE");
        options.put("liveness_control", "LOW");

        // 人脸检测   options：人脸检测配置信息
        JSONObject res = client.detect(image, imageType, options);
        System.out.println(res.toString(2));
        //主要判断就是error_code是否为0,如果为0则是人脸
        Object error_code = res.get("error_code");
        System.out.println(error_code);
    }
}
