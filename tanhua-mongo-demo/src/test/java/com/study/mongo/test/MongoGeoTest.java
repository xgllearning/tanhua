package com.study.mongo.test;

import com.study.mongo.domain.Places;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJson;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoGeoTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    //查询附近的地理内容
    @Test
    public void testNear(){
        //1.构建坐标原点
        GeoJsonPoint jsonPoint = new GeoJsonPoint(116.404, 39.915);
        //2.构造半径范围,Metrics
        Distance distance = new Distance(1, Metrics.KILOMETERS);
        //3.画一个圆圈
        Circle circle = new Circle(jsonPoint,distance);
        //4.通过mongodb进行查询，构造查询条件,withinSphere(circle)
        Query query = Query.query(Criteria.where("location").withinSphere(circle));
        List<Places> places = mongoTemplate.find(query, Places.class);
        places.forEach(System.out::println);
    }

    //搜索附件并能返回距离
    @Test
    public void GeoNear(){
        //1.构造坐标原点
        GeoJsonPoint jsonPoint = new GeoJsonPoint(116.404, 39.915);
        //2.构造NearQuery对象
        NearQuery nearQuery = NearQuery.near(jsonPoint, Metrics.KILOMETERS).maxDistance(1, Metrics.KILOMETERS);
        //3.调用mongoDb的geoNear查询
        GeoResults<Places> results = mongoTemplate.geoNear(nearQuery, Places.class);
        //4.解析GeoResults,获取距离数据
        for (GeoResult<Places> result : results) {
            Places content = result.getContent();
            System.out.println(content);
            Distance distance = result.getDistance();
            System.out.println(distance);
        }
    }
}