package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Visitors;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Objects;

@DubboService
public class VisitorsApiImpl implements VisitorsApi{

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存访客记录
     * @param visitor
     */
    @Override
    public void save(Visitors visitor) {
        //1.根据userId、visitorUserId、visitDate唯一确定一条数据，如果该数据存在，不保存，如果不存在则保存
        Query query = Query.query(Criteria.where("userId").is(visitor.getUserId())
                .and("visitorUserId").is(visitor.getVisitorUserId())
                .and("visitDate").is(visitor.getVisitDate()));
        boolean exists = mongoTemplate.exists(query, Visitors.class);
        //2、不存在，保存
        if (!exists){
            mongoTemplate.save(visitor);
        }
    }
    /**
     * 查询我的访客数据，存在2种情况：
     * 1. 我没有看过我的访客数据，返回前5个访客信息
     * 2. 之前看过我的访客，从上一次查看的时间点往后查询5个访客数据
     * @param date 上一次查询时间
     * @return
     */
    @Override
    public List<Visitors> queryMyVisitors(Long date, Long userId) {
        //根据当前用户id查询来访问过的用户
        Criteria criteria = Criteria.where("userId").is(userId);

        if (date!=null){//如果不为null,则查询大于当前时间的用户
            criteria.and("date").gt(date);
        }
        //如果date为null,说明redis中没有数据，即此用户第一次查看访问数据，此时查询全部用户，只返回5条
        Query query = Query.query(criteria).limit(5).with(Sort.by(Sort.Order.desc("date")));
        List<Visitors> visitors = mongoTemplate.find(query, Visitors.class);
        return visitors;

    }
}
