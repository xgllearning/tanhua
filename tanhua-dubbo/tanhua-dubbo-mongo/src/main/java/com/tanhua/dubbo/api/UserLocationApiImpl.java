package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.UserLocation;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

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
}
