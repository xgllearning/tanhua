package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.vo.CommentVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {

    @DubboReference
    private CommentApi commentApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    /**
     * 分页查询评理列表
     * @param movementId
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult findComments(String movementId, Integer page, Integer pagesize) {
        //1.分页查询评论列表
        List<Comment> list=commentApi.findComments(movementId,CommentType.COMMENT,page,pagesize);
        //2.判断list集合是否存在
        if (CollUtil.isEmpty(list)){
            return new PageResult();
        }
        //3.提取所有的评论用户id,通过工具类生成list集合
        List<Long> userIds = CollUtil.getFieldValues(list, "userId", Long.class);
        //4.通过userInfoApi查询出评论者详细信息
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        //5.构造vo对象，封装返回的对象，List<CommentVo>
        List<CommentVo> commentVos = new ArrayList<>();
        for (Comment comment : list) {
            UserInfo userInfo = map.get(comment.getUserId());
            if (userInfo!=null){
                CommentVo commentVo = CommentVo.init(userInfo, comment);
                commentVos.add(commentVo);
            }
        }
        //构造返回值
        return new PageResult(page,pagesize, Math.toIntExact(0l),commentVos);
    }



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
