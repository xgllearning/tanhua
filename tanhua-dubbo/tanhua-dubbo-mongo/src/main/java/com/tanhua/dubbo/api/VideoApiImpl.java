package com.tanhua.dubbo.api;

import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.model.mongo.Video;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

@DubboService
public class VideoApiImpl implements VideoApi{

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private MongoTemplate mongoTemplate;
    /**
     * 保存上传视频文件数据
     * @param video
     * @return
     */
    @Override
    public String save(Video video) {
        //继续封装video
        video.setVid(idWorker.getNextId("video"));
        video.setCreated(System.currentTimeMillis());
        //2、调用方法保存对象
        mongoTemplate.save(video);
        //3、返回对象id
        return video.getId().toHexString();
    }
}
