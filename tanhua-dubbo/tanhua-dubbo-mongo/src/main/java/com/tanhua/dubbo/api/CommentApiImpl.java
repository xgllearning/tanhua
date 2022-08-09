package com.tanhua.dubbo.api;

import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Movement;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Objects;

@DubboService
public class CommentApiImpl implements CommentApi{

    @Autowired
    private MongoTemplate mongoTemplate;

    //发布评论，并获取评论数量
    public Integer save(Comment comment) {
        //1.想办法封装Comment对象中的publishUserId被评论人ID，根据动态id查询动态进而获取该用户id
        //2.查询动态,根据动态id查询动态
        Movement movement = mongoTemplate.findById(comment.getPublishId(), Movement.class);
        //3.判断并设置被评论人id
        if (Objects.nonNull(movement)){
            comment.setPublishUserId(movement.getUserId());
        }
        //4.保存到数据库
        mongoTemplate.save(comment);
        //5.更新动态表中评论的数量，并返回更新后的字段,根据动态id查询动态
        Query query = Query.query(Criteria.where("id").is(comment.getPublishId()));
        //6. findAndModify更新某个字段，并返回更新后的数值，参数一查询条件，参数二更新的字段和数值，参数三更新的设置属性，可以指定拿到更新后的结果，参数四：当前操作的字节码class
        Update update = new Update();
        if(comment.getCommentType() == CommentType.LIKE.getType()) {
            update.inc("likeCount",1);
        }else if (comment.getCommentType() == CommentType.COMMENT.getType()){
            update.inc("commentCount",1);
        }else {
            update.inc("loveCount",1);
        }
        //设置更新参数
        FindAndModifyOptions options = new FindAndModifyOptions();
        //获取更新后的最新数据
        options.returnNew(true);
        Movement modify = mongoTemplate.findAndModify(query, update, options, Movement.class);
        //获取最新的评论数量，并返回
        return modify.statisCount(comment.getCommentType());
    }
}
