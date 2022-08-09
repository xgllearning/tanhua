package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Comment;

public interface CommentApi {
    /**
     * 发布评论
     * @param comment
     * @return
     */
    Integer save(Comment comment);
}
