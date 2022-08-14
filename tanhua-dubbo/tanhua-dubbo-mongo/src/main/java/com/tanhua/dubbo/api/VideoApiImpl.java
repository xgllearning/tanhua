package com.tanhua.dubbo.api;

import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.model.mongo.Video;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.naming.ldap.SortControl;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * 根据vid查询
     * @param vidList
     * @return
     */
    @Override
    public List<Video> findMovementsByVids(List<Long> vidList) {
        Query query = Query.query(Criteria.where("vid").in(vidList));
        List<Video> videos = mongoTemplate.find(query, Video.class);
        return videos;
    }

    /**
     * 分页查询
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public List<Video> queryVideoList(int page, Integer pagesize) {
        Query query = new Query().skip((page-1)*pagesize).limit(pagesize).with(Sort.by(Sort.Order.desc("created")));
        List<Video> videos = mongoTemplate.find(query, Video.class);
        return videos;
    }
}
