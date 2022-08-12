package com.tanhua.dubbo.api;

import com.tanhua.model.domain.Announcement;

import java.util.List;

public interface AnnouncementsApi {
    //查询公共列表
    List<Announcement> find(Integer page, Integer pagesize);
}
