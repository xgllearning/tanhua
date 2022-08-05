package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.RecommendUser;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
@DubboService
public class RecommendUserApiImpl implements RecommendUserApi{

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 查询今日佳人数据,根据toUserId进行查询(当前用户),按照分数递减排序，返回第一名
     * @param toUserId
     * @return
     */
    @Override
    public RecommendUser queryWithMaxScore(Long toUserId) {
        //构造查询条件，首先创建Criteria对象.where("数据库字段").is(传递参数)
        Criteria criteria=Criteria.where("toUserId").is(toUserId);
        //创建Query对象,传入criteria,并with排序
        Query query = Query.query(criteria).with(Sort.by(Sort.Order.desc("score")));
        //调用mongoTemplate的findOne查询方法
        RecommendUser recommendUser = mongoTemplate.findOne(query, RecommendUser.class);
        return recommendUser;
    }
}
