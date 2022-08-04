package com.tanhua.autoconfig.template;

import com.baidu.aip.face.AipFace;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

public class AipFaceTemplate {
    //注入AipFace的ioc容器bean对象
    @Autowired
    private AipFace client;

    /**
     * 检测图片中是否包含人脸
     * 传入一张图片的url
     *  true：包含
     *  false：不包含
     */
    public boolean detect(String imageUrl) {
        // 调用接口
        String imageType = "URL";
        //String image = "https://tanhua001.oss-cn-beijing.aliyuncs.com/2021/04/19/a3824a45-70e3-4655-8106-a1e1be009a5e.jpg";
        //如果想将上传的imageUrl设置为刘德华头像进行人脸识别-则放开，此时传入的是刘德华照片
        //imageUrl="https://tanhua001.oss-cn-beijing.aliyuncs.com/2021/04/19/a3824a45-70e3-4655-8106-a1e1be009a5e.jpg";
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("face_field", "age");
        options.put("max_face_num", "2");
        options.put("face_type", "LIVE");
        options.put("liveness_control", "LOW");

        // 人脸检测
        JSONObject res = client.detect(imageUrl, imageType, options);
        System.out.println(res.toString(2));

        Integer error_code = (Integer) res.get("error_code");
        //  等于0为true
        return error_code == 0;
    }
}
