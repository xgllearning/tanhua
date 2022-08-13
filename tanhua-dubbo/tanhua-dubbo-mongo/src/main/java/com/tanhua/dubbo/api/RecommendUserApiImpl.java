package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.mongo.UserLike;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

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
        Query query = Query.query(criteria).with(Sort.by(Sort.Order.desc("score"))).limit(1);//查询条数;
        //调用mongoTemplate的findOne查询方法
        RecommendUser recommendUser = mongoTemplate.findOne(query, RecommendUser.class);
        return recommendUser;
    }
    /**
     * 推荐好友列表
     * @param page
     * @param pagesize
     * @param toUserId
     * @return
     */
    @Override
    public PageResult queryRecommendUserList(Integer page, Integer pagesize, Long toUserId) {
        //1.构建Criteria对象,查询条件
        Criteria criteria = Criteria.where("toUserId").is(toUserId);
        //2.构建query对象
        Query query = Query.query(criteria);
        //3.调用mongoTemplate查询总数(如果有查询条件,则先查总数再查数据列表,如果没有条件,可以共用一个query)
        long count = mongoTemplate.count(query, RecommendUser.class);
        //4.调用mongoTemplate查询数据列表
        query.limit(pagesize).skip((page-1)*pagesize).with(Sort.by(Sort.Order.desc("score")));
        List<RecommendUser> recommendUsers = mongoTemplate.find(query, RecommendUser.class);
        //5.构造返回值
        return new PageResult(page, pagesize,  Math.toIntExact(count), recommendUsers);
    }

    /**
     * 联系人管理-查询佳人详情信息
     * @param userId
     * @param toUserId
     * @return
     */
    @Override
    public RecommendUser queryByUserId(Long userId, Long toUserId) {
        //1.通过mongoTemplate查询
        Criteria criteria = Criteria.where("userId").is(userId).and("toUserId").is(toUserId);
        Query query = Query.query(criteria);
        RecommendUser recommendUser = mongoTemplate.findOne(query, RecommendUser.class);
        //recommendUser可能查询不到，构建一个返回
        if(recommendUser == null) {
            recommendUser = new RecommendUser();
            recommendUser.setUserId(userId);
            recommendUser.setToUserId(toUserId);
            //构建缘分值
            recommendUser.setScore(95d);
        }
        return recommendUser;
    }
    /**
     * 查询推荐用户，需要排除喜欢和不喜欢的用户
     * 1、排除喜欢，不喜欢的用户
     * 2、随机展示
     * 3、指定数量
     * @param userId
     * @param count
     * @return
     */
    @Override
    public List<RecommendUser> queryCardsList(Long userId, int count) {
        //1.查询喜欢和不喜欢的表中的推荐用户id
        Query query = Query.query(Criteria.where("userId").is(userId));
        List<UserLike> userLikes = mongoTemplate.find(query, UserLike.class);
        List<Long> likeUserIds = CollUtil.getFieldValues(userLikes, "likeUserId", Long.class);
        //2.获取到所有的喜欢和不喜欢的推荐用户id,再查询推荐用户，把这些id排除掉,nin查询所有，但是要排除某些数据
        Criteria criteria = Criteria.where("toUserId").is(userId).and("userId").nin(likeUserIds);
        //3.随机展示推荐用户列表，需要使用统计函数
        TypedAggregation<RecommendUser> newAggregation = TypedAggregation.newAggregation(RecommendUser.class,
                Aggregation.match(criteria),//指定查询条件
                Aggregation.sample(count));//随机获取几条数据

        AggregationResults<RecommendUser> result = mongoTemplate.aggregate(newAggregation, RecommendUser.class);
        //构造返回
        return result.getMappedResults();
    }
}
