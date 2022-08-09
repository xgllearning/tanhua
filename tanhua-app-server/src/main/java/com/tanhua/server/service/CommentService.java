package com.tanhua.server.service;

import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    @DubboReference
    private CommentApi commentApi;

    /**
     * 发布评论
     * @param movementId
     * @param comment
     */
    public void saveComments(String movementId, String comment) {
        //1.封装评论对象id、publishId、commentType、content、userId、publishUserId、created;
        Comment comment1 = new Comment();
        comment1.setContent(comment);
        comment1.setCreated(System.currentTimeMillis());
        comment1.setUserId(UserHolder.getUserId());
        comment1.setCommentType(CommentType.COMMENT.getType());
        comment1.setPublishId(new ObjectId(movementId));
        //comment1.setPublishUserId();被评论人id即发布动态人的id,后续通过api进行查询封装
        //2、调用API保存评论,返回最新的评论数量
        Integer commentCount = commentApi.save(comment1);
    }
}
