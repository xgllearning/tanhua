package com.tanhua.dubbo.utils;


import com.tanhua.model.mongo.Sequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
//满足原子性，不存在线程安全问题
@Component
public class IdWorker {
    //对sequence集合进行操作
    @Autowired
    private MongoTemplate mongoTemplate;

    public Long getNextId(String collName) {
        Query query = new Query(Criteria.where("collName").is(collName));

        Update update = new Update();
        //inc是对seqId该字段加几
        update.inc("seqId", 1);
        //选项配置对象
        FindAndModifyOptions options = new FindAndModifyOptions();
        //如果不存在会保存一条数据
        options.upsert(true);
        //每次返回最新的数据内容
        options.returnNew(true);
        //通过mongoTemplate进行更新，先进行修改，把修改后最新数据返回，对Sequence表进行操作
        Sequence sequence = mongoTemplate.findAndModify(query, update, options, Sequence.class);
        return sequence.getSeqId();
    }
}
