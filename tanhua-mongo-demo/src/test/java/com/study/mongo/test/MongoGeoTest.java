package com.study.mongo.test;

import com.study.mongo.domain.Places;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJson;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
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
}