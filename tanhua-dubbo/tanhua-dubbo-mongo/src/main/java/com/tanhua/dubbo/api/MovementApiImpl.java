package com.tanhua.dubbo.api;

import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.dubbo.utils.TimeLineService;
import com.tanhua.model.mongo.Friend;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.MovementTimeLine;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@DubboService
public class MovementApiImpl implements MovementApi{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TimeLineService timeLineService;
    /**
     * 发布动态
     * @param movement
     */
    @Override
    public void publish(Movement movement) {
        try {
            //1.保存动态表,继续封装movement数据，缺少pid,created
            //设置PID
            movement.setPid(idWorker.getNextId("movement"));
            //可以手动设置ObjectId
            //movement.setId(ObjectId.get());
            //设置时间
            movement.setCreated(System.currentTimeMillis());
            //保存数据
            mongoTemplate.save(movement);
            timeLineService.saveTimeLine(movement.getUserId(),movement.getId(), movement.getCreated());
            //TODO：抽取以下代码，异步执行，解决大量的时间线数据同步写入的问题
//            //2.查询好友表,查询当前用户的好友-返回List列表
//            Criteria criteria = Criteria.where("userId").is(movement.getUserId());
//            Query query = Query.query(criteria);
//            List<Friend> friends = mongoTemplate.find(query, Friend.class);
//            //循环好友数据，构建时间线数据存入数据库，根据好友id保存时间线表
//            for (Friend friend : friends) {
//                MovementTimeLine timeLine = new MovementTimeLine();
//                timeLine.setMovementId(movement.getId());
//                timeLine.setUserId(friend.getUserId());
//                timeLine.setFriendId(friend.getFriendId());
//                timeLine.setCreated(movement.getCreated());
//                mongoTemplate.save(timeLine);
//            }
        } catch (Exception e) {
            //忽略事务处理
            e.printStackTrace();
        }
    }

    @Override
    public PageResult findByUserId(Long userId, Integer page, Integer pagesize) {
        //创建Criteria
        Criteria criteria = Criteria.where("userId").is(userId);
        //创建Query对象
        Query query = Query.query(criteria);
        //查询总记录数
        long count = mongoTemplate.count(query, Movement.class);
        //设置分页查询参数
        query.skip((page -1 ) * pagesize).limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        //查询分页数据列表
        List<Movement> movements = mongoTemplate.find(query, Movement.class);
        //构造返回值
        return new PageResult(page,pagesize, Math.toIntExact(count),movements);
    }
}
