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

    /**
     * 调用API随机构造10条动态数据
     * @param count
     * @return
     */
    List<Movement> randomMovements(Integer count);

    /**
     * 调用API根据PID数组查询动态数据
     * @param pids
     * @return
     */
    List<Movement> findMovementsByPids(List<Long> pids);
    //查看单条动态
    Movement findById(String movementId);
}
