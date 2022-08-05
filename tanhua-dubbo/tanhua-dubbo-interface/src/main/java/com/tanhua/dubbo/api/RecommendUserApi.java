package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.RecommendUser;

public interface RecommendUserApi {
    //查询今日佳人数据,根据toUserId进行查询(当前用户),按照分数递减排序，返回第一名
    RecommendUser queryWithMaxScore(Long toUserId);
}
