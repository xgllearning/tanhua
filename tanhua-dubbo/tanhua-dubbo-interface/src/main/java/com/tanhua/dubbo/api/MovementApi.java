package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.PageResult;

import java.util.List;

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

    /**
     * 查询当前用户的好友动态
     * @param page
     * @param pagesize
     * @param userId
     * @return
     */
    List<Movement> findFriendMovements(Integer page, Integer pagesize, Long userId);

}
