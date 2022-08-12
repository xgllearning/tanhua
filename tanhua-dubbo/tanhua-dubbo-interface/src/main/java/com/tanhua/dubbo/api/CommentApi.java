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

    /**
     * 判断comment数据是否存在,查询该用户是否已对该动态点赞
     * @param movementId
     * @param userId
     * @param like
     * @return
     */
    Boolean hasComment(String movementId, Long userId, CommentType like);

    /**
     * 取消点赞
     * @param comment
     * @return
     */
    Integer delete(Comment comment);

    /**
     * 评论点赞
     * @param comment
     * @return
     */
    Integer saveComment(Comment comment);

    /**
     * 取消评论点赞
     * @param comment
     * @return
     */
    Integer deleteComment(Comment comment);

    /**
     * 查询当前用户点赞列表数据
     * @param userId
     * @param like
     * @param page
     * @param pagesize
     * @return
     */
    List<Comment> findLikeComments(Long userId, CommentType like, Integer page, Integer pagesize);
}
