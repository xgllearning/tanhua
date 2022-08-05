package com.study.mongo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(value="tb_person")//指定实体类和mongo集合的映射关系，不写的话，默认操作的当前实体类名的集合(表)
public class Person {

    @Id//映射集合中的id主键
    private ObjectId id;
    @Field("myname")//字段名与属性名对应
    private String name;
    private Integer age;
    private String address;
    
}