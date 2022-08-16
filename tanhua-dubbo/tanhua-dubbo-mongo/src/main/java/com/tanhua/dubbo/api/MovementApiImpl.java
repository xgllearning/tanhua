package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.dubbo.utils.TimeLineService;
import com.tanhua.model.mongo.Friend;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.MovementTimeLine;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
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
public class MovementApiImpl implements MovementApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TimeLineService timeLineService;

    /**
     * 发布动态
     *
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
            timeLineService.saveTimeLine(movement.getUserId(), movement.getId(), movement.getCreated());
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
        query.skip((page - 1) * pagesize).limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        //查询分页数据列表
        List<Movement> movements = mongoTemplate.find(query, Movement.class);
        //构造返回值
        return new PageResult(page, pagesize, Math.toIntExact(count), movements);
    }

    /**
     * 查询当前用户好友发布的所有动态
     *
     * @param friendId:当前操作用户id 即根据当前用户id查询出其好友的动态，其在时间线表中代表的是好友id，因为是查询当前用户的好友动态，当前用户是作为其他用户的好友
     */
    @Override
    public List<Movement> findFriendMovements(Integer page, Integer pagesize, Long friendId) {
        //1.先查询时间线表,构造查询条件,查询出所有的好友动态
        Query query = Query.query(Criteria.where("friendId").is(friendId)).skip((page - 1) * pagesize).limit(pagesize)
                .with(Sort.by(Sort.Order.asc("created")));
        List<MovementTimeLine> timeLines = mongoTemplate.find(query, MovementTimeLine.class);
        //2.将查询出来的好友动态的动态id得到形成新集合
        List<ObjectId> movementsId = CollUtil.getFieldValues(timeLines, "movementId", ObjectId.class);
        //3.根据动态id从动态表中查询出动态详情Movement
        Query movementQuery = Query.query(Criteria.where("id").in(movementsId));
        return mongoTemplate.find(movementQuery, Movement.class);
    }

    /**
     * 调用API随机构造10条动态数据
     * MongoDB获取随机数
     * Aggregation.sample()设置随机采样数
     * aggregate()方法统计
     * @param count
     * @return
     */
    @Override
    public List<Movement> randomMovements(Integer count) {
        //MongoDB获取随机数
        //1.创建统计对象，设置统计参数,参数一：操作哪个实体类就写哪个实体类的class,参数二：调用的统计方法
        TypedAggregation<Movement> aggregation = Aggregation.newAggregation(Movement.class, Aggregation.sample(count));
        //2.调用mongoTemplate方法统计，Movement.class目的是将结果设置到该对象中
        AggregationResults<Movement> results = mongoTemplate.aggregate(aggregation, Movement.class);
        //3.获取统计结果
        List<Movement> mappedResults = results.getMappedResults();
        return mappedResults;
    }

    /**
     * 调用API根据PID数组查询动态数据
     * @param pids
     * @return
     */
    @Override
    public List<Movement> findMovementsByPids(List<Long> pids) {
        Query query = Query.query(Criteria.where("pid").in(pids));

        return mongoTemplate.find(query,Movement.class);
    }

    @Override
    public Movement findById(String movementId) {
        return mongoTemplate.findById(movementId,Movement.class);
    }

    /**
     * 多条件查询动态--用户管理(根据用户id)和动态审核中(根据状态，查询全部、查询审核过的、查询未审核过的、查询审核失败的)
     * @param page
     * @param pagesize
     * @param uid
     * @param state
     * @return
     */
    @Override
    public PageResult findByUidOrState(Integer page, Integer pagesize, Long uid, Integer state) {
        //1.因为是多条件查询，因此通过new Query，根据条件不同添加Criteria条件
        Query query = new Query();
        //2.当用户id不为null时，说明的是查询当前用户动态
        if(uid!=null){
            query.addCriteria(Criteria.where("userId").is(uid));
        }
        //3.当状态不为null时，就是根据审核状态进行查询
        if (state!=null){
            query.addCriteria(Criteria.where("state").is(state));
        }
        //分页、排序
        query.skip((page-1)*pagesize).limit(pagesize).with(Sort.by(Sort.Order.desc("created")));
        //调用mongoTemplate进行查询
        long count = mongoTemplate.count(query, Movement.class);
        List<Movement> movements = mongoTemplate.find(query, Movement.class);

        return new PageResult(page,pagesize, Math.toIntExact(count),movements);
    }

}
