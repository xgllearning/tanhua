package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Visitors;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

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
}
