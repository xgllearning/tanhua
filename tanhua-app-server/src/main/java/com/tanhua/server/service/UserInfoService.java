package com.tanhua.server.service;

import com.tanhua.autoconfig.template.AipFaceTemplate;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserInfoService {
    //注入UserInfoApi接口实现保存
    @DubboReference
    private UserInfoApi userInfoApi;
    //注入阿里云上传图片
    @Autowired
    private OssTemplate ossTemplate;
    //注入百度云人脸识别
    @Autowired
    private AipFaceTemplate aipFaceTemplate;

    /**完善用户信息
     */
    public void save(UserInfo userInfo) {
        userInfoApi.save(userInfo);
    }

    public void updateHead(MultipartFile headPhoto, Integer id) {

        try {
            //1.根据headPhoto上传图片到阿里云,并返回图片url地址
            String imageUrl = ossTemplate.upload(headPhoto.getOriginalFilename(), headPhoto.getInputStream());
            //2.调用百度云进行人脸验证
            boolean detect = aipFaceTemplate.detect(imageUrl);
            //2.1如果不包含人脸，抛出异常
            if (!detect){
                throw new RuntimeException("不包含人脸");
            }else{
                //2.2包含人脸，调用Api更新
                UserInfo userInfo = new UserInfo();
                //设置id
                userInfo.setId(Long.valueOf(id));
                //设置头像链接地址
                imageUrl="https://tanhua001.oss-cn-beijing.aliyuncs.com/2021/04/19/a3824a45-70e3-4655-8106-a1e1be009a5e.jpg";
                userInfo.setAvatar(imageUrl);
                userInfoApi.update(userInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 根据ID查询个人资料(详细信息)
     * @param userID
     * @return
     */
    public UserInfo findById(Long userID) {
        UserInfo userInfo=userInfoApi.findById(userID);

        return userInfo;
    }
}