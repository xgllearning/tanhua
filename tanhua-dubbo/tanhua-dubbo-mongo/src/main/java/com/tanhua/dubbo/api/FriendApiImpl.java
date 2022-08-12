package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Friend;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@DubboService
public class FriendApiImpl implements FriendApi{

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 添加好友
     * @param userId
     * @param friendId
     */
    @Override
    public void save(Long userId, Long friendId) {
        //1.是两条双向数据，根据用户id和好友id唯一确定一条数据，首先查询是否存在记录，再进行添加
        //用户-->好友
        Criteria criteria = Criteria.where("userId").is(userId).and("friendId").is(friendId);
        Query query = Query.query(criteria);
        //2.查询数据关系是否存在，如果不存在则保存
        boolean exists = mongoTemplate.exists(query, Friend.class);
        if (!exists){
            Friend friend = new Friend();
            friend.setUserId(userId);
            friend.setFriendId(friendId);
            friend.setCreated(System.currentTimeMillis());
            mongoTemplate.save(friend);
        }
        //3、保存好友-->用户的数据
        Query query2 = Query.query(Criteria.where("userId").is(friendId).and("frinedId").is(userId));
        //3.1 判断好友关系是否存在
        if(!mongoTemplate.exists(query2, Friend.class)) {
            //2.2 如果不存在，保存
            Friend friend = new Friend();
            friend.setUserId(friendId);
            friend.setFriendId(userId);
            friend.setCreated(System.currentTimeMillis());
            mongoTemplate.save(friend);
        }
    }
}
