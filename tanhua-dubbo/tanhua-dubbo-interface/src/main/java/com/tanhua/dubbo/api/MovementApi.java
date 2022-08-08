package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.PageResult;

public interface MovementApi {

    /**
     * 发布动态
     * @param movement
     */
    void publish(Movement movement);

    /**
     * 查询我的动态
     * @param userId
     * @param page
     * @param pagesize
     * @return
     */
    PageResult findByUserId(Long userId, Integer page, Integer pagesize);
}
