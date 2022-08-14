package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Video;

import java.util.ArrayList;
import java.util.List;

public interface VideoApi {
    //保存上传视频信息
    String save(Video video);
    //根据vid进行查询video
    List<Video> findMovementsByVids(List<Long> vidList);
    //分页查询video
    List<Video> queryVideoList(int page, Integer pagesize);
}
