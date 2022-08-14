package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.dubbo.api.VideoApi;
import com.tanhua.model.mongo.Video;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class SmallVideosService {

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private FastFileStorageClient client;

    @Autowired
    private FdfsWebServer fdfsWebServer;

    @DubboReference
    private VideoApi videoApi;
    /**
     * 上传视频到服务器，通过fastDfs上传大文件，oss上传小文件图片
     * @param videoThumbnail
     * @param videoFile
     */
    public void saveVideos(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        //1.首先判断前台有没有传过来文件，如果没有传递则抛出异常
        if(videoThumbnail.isEmpty()||videoFile.isEmpty()){
            throw new BusinessException(ErrorResult.error());
        }
        //2.目的是封装video对象
        //2.1操作视频videoFile，获取其后缀
        String filename = videoFile.getOriginalFilename();
        filename = filename.substring(filename.lastIndexOf(".") + 1);
        StorePath storePath = client.uploadFile(videoFile.getInputStream(), videoFile.getSize(), filename, null);
        //2.2拼接视频完整路径
        String videoUrl = fdfsWebServer.getWebServerUrl()+storePath.getFullPath();
        Video video = new Video();
        video.setVideoUrl(videoUrl);
        //2.3操作videoThumbnail图片url
        String picUrl = ossTemplate.upload(videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());
        video.setPicUrl(picUrl);
        video.setUserId(UserHolder.getUserId());
        video.setText("我就是我，不一样的烟花");
        //3.调用api保存数据,通过保存成功返回id来判断是否保存成功，保存失败则抛出异常
        String videoId=videoApi.save(video);
        if (StringUtils.isEmpty(videoId)){
            throw new BusinessException(ErrorResult.error());
        }

    }
}
