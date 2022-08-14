package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Video;

public interface VideoApi {
    //保存上传视频信息
    String save(Video video);


}
