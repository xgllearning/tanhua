package com.study.mongo.test;

import com.study.mongo.MongoApplication;
import com.study.mongo.domain.Person;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MongoTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    //保存
    @Test
    public void testSave() {
        Person person = new Person();
        person.setName("李六");
        person.setAge(18);
        mongoTemplate.save(person);
    }

    //查询-查询所有
    @Test
    public void testFindAll() {
        //查询时，需要传递你所操作集合的字节码
        List<Person> list = mongoTemplate.findAll(Person.class);
        for (Person person : list) {
            System.out.println(person);
        }
    }

    /**
     * 条件查询
     * 查询myname=xxx的和age=18
     * +组合查询
     */
    @Test
    public void testFind() {
        //TODO:步骤1.创建Criteria对象，并设置查询条件,设置查询条件时，where中设置的是表中的字段名，而不是实体属性名
        Criteria criteria = Criteria.where("myname").is("张三").and("age").is(18);
        //步骤2.根据Criteria创建Query对象
        Query query = new Query(criteria);
        //步骤3.mongoTemplate.find查询,参数一：Query对象,参数二：封装的实体类对象
        List<Person> list = mongoTemplate.find(query, Person.class);
        for (Person person : list) {
            System.out.println(person);
        }
    }
    /**
     * 条件查询
     * age<20的
     */
    @Test
    public void testFind01() {
        //查询年龄小于20的所有人
        Query query = new Query(Criteria.where("age").lt(20)); //查询条件对象
        //find查询,参数一：Query对象,参数二：封装的实体类对象
        List<Person> list = mongoTemplate.find(query, Person.class);

        for (Person person : list) {
            System.out.println(person);
        }
    }

    /**
     * 分页查询,需要page,size
     * page=1,size=2
     */
    @Test
    public void testPage() {
        int page=1;
        int size=2;
        Criteria criteria = Criteria.where("age").lt(30);
        //1 查询总数
        Query queryCount = new Query(criteria);
        long count = mongoTemplate.count(queryCount, Person.class);
        System.out.println(count);
        //2 查询当前页的数据列表, 查询第二页，每页查询2条
        Query queryLimit = new Query(criteria)
                .limit(size)//设置每页查询条数
                .skip((page-1)*size)//开启查询的条数,即从第几条开始查 （page-1）*size
                .with(Sort.by(Sort.Order.desc("age"))); //排序条件,根据年龄降序

        List<Person> list = mongoTemplate.find(queryLimit, Person.class);
        for (Person person : list) {
            System.out.println(person);
        }
    }
    /**
     * 分页查询
     */
    @Test
    public void testPage01() {
        Criteria criteria = Criteria.where("age").lt(30);
        //1 查询总数
        Query queryCount = new Query(criteria);
        long count = mongoTemplate.count(queryCount, Person.class);
        System.out.println(count);
        //2 查询当前页的数据列表, 查询第二页，每页查询2条
        Query queryLimit = new Query(criteria)
                .limit(2)//设置每页查询条数
                .skip(2) ; //开启查询的条数 （page-1）*size
        List<Person> list = mongoTemplate.find(queryLimit, Person.class);
        for (Person person : list) {
            System.out.println(person);
        }
    }

    /**
     * 更新:
     *    根据id，更新年龄
     */
    @Test
    public void testUpdate() {
        //1 query条件
        Query query = Query.query(Criteria.where("id").is("5fe404c26a787e3b50d8d5ad"));
        //2 更新的数据
        Update update = new Update();
        update.set("age", 20);
        //TODO：updateFirst更新一条记录，updateMulti更新多条记录，参数一：条件，参数二：要更新的数据内容，参数三：要操作的实体类字节码
        mongoTemplate.updateFirst(query, update, Person.class);

    }

    /**
     * 删除
     */
    @Test
    public void testRemove() {
        //1 query条件
        Query query = Query.query(Criteria.where("id").is("5fe404c26a787e3b50d8d5ad"));
        mongoTemplate.remove(query, Person.class);
    }
}


