package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Visitors;

public interface VisitorsApi {
    //保存访客记录
    void save(Visitors visitor);
}
