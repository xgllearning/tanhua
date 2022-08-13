package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.model.mongo.UserLocation;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

@DubboService
public class UserLocationApiImpl implements UserLocationApi{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Boolean updateLocation(Long userId, Double longitude, Double latitude, String address) {
        try {
            //1.根据userId查询当前位置数据是否已经存在
            Query query = Query.query(Criteria.where("userId").is(userId));
            UserLocation userLocation = mongoTemplate.findOne(query, UserLocation.class);
            if (userLocation==null){
                //执行保存操作
                //2、如果不存在用户位置信息，保存
                userLocation = new UserLocation();
                userLocation.setUserId(userId);
                userLocation.setAddress(address);
                userLocation.setCreated(System.currentTimeMillis());
                userLocation.setUpdated(System.currentTimeMillis());
                userLocation.setLastUpdated(System.currentTimeMillis());
                userLocation.setLocation(new GeoJsonPoint(longitude,latitude));//GeoJsonPoint不能序列化，所以无法在提供者端和消费端传递
                mongoTemplate.save(userLocation);
            }else {
                //执行更新操作
                Update update = Update.update("location", new GeoJsonPoint(longitude, latitude))
                        .set("address",address)
                        .set("updated", System.currentTimeMillis())
                        .set("lastUpdated", userLocation.getUpdated());
                mongoTemplate.updateFirst(query,update,UserLocation.class);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 查询user_Location表，根据当前用户id查询出在范围内的所有用户id,此时包含当前用户id
     * @param userId
     * @param gender
     * @param distance
     * @return
     */
    @Override
    public List<Long> queryNearUser(Long userId, String gender, String distance) {
        //1.查询当前用户的信息
        Query query = Query.query(Criteria.where("userId").is(userId));
        UserLocation user = mongoTemplate.findOne(query, UserLocation.class);
        if (user==null){
            return null;
        }
        //2.得到当前用户的地理信息,以此为原点
        GeoJsonPoint location = user.getLocation();
        //3.绘制半径
        Double metre = Double.valueOf(distance);
        Distance radius = new Distance(metre / 1000, Metrics.KILOMETERS);
        //4.绘制查询范围圆
        Circle circle = new Circle(location, radius);
        //查询
        Query locationQuery = Query.query(Criteria.where("location").withinSphere(circle));
        List<UserLocation> list = mongoTemplate.find(locationQuery, UserLocation.class);
        List<Long> ids = CollUtil.getFieldValues(list, "userId", Long.class);
        return ids;
    }
}
