package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.PageResult;

public interface RecommendUserApi {
    //查询今日佳人数据,根据toUserId进行查询(当前用户),按照分数递减排序，返回第一名
    RecommendUser queryWithMaxScore(Long toUserId);

    /**
     * 推荐好友列表
     * @param page
     * @param pagesize
     * @param toUserId
     * @return
     */
    PageResult queryRecommendUserList(Integer page, Integer pagesize, Long toUserId);

    /**
     * 联系人管理-查询佳人详情信息
     * @param userId
     * @param toUserId
     * @return
     */
    RecommendUser queryByUserId(Long userId, Long toUserId);
}
