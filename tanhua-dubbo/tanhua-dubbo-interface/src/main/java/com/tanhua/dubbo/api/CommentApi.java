package com.tanhua.dubbo.api;

import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;

import java.util.List;

public interface CommentApi {
    /**
     * 发布评论
     * @param comment
     * @return
     */
    Integer save(Comment comment);

    /**
     * 分页查询评论列表
     * @param movementId
     * @param comment
     * @param page
     * @param pagesize
     * @return
     */
    List<Comment> findComments(String movementId, CommentType comment, Integer page, Integer pagesize);
}
