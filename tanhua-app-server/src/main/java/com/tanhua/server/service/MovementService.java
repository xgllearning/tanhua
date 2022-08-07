package com.tanhua.server.service;

import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;

@Service
public class MovementService {


    @Autowired
    private OssTemplate ossTemplate;

    @DubboReference
    private MovementApi movementApi;

    /**
     * 发布动态，imageContet-可以携带多张文件，采用数组
     *textContent文字动态
     *location位置longitude经度
     *latitude纬度
     *其余通过对象接收Movement
     * @param movement
     * @param imageContent
     */
    public void publishMovement(Movement movement, MultipartFile[] imageContent) throws IOException {
        //1.判断参数textContent文字动态是否为空，如果为空则抛出自定义异常
        if (StringUtils.isEmpty(movement.getTextContent())){
            throw new BusinessException(ErrorResult.contentError());
        }
        //2.获取当前用户id,保存内容到动态表,但Movement需要进行封装,pid、created、userId、medias
        Long userId = UserHolder.getUserId();
        ArrayList<String> medias = new ArrayList<>();
        //3.遍历imageContent，通过ossTemplate上传图片，进而获取url地址，封装进Movement
        for (MultipartFile multipartFile : imageContent) {
            String upload = ossTemplate.upload(multipartFile.getOriginalFilename(), multipartFile.getInputStream());
            medias.add(upload);
        }
        //4、将数据封装到Movement对象
        movement.setUserId(userId);
        movement.setMedias(medias);
        //5、调用movementApi完成发布动态
        movementApi.publish(movement);
    }
}
