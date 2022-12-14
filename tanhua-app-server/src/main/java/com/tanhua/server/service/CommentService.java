package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.vo.CommentVo;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @DubboReference
    private CommentApi commentApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    /**
     * 分页查询评论列表
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

                String key = Constants.MOVEMENTS_INTERACT_KEY + comment.getId().toHexString();//唯一标识该动态，在该动态下可以有很多用户点赞
                String hashKey = Constants.MOVEMENT_LIKE_HASHKEY + UserHolder.getUserId();//唯一标识该动态点赞的用户
                //判断redis中是否存在
                Boolean commentLike = redisTemplate.opsForHash().hasKey(key, hashKey);
                if (commentLike){
                    commentVo.setHasLiked(1);
                }
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

    /**点赞
     * comment中是根据commentType判断是点赞还是评论还是喜欢
     * @param movementId
     * @return
     */
    public Integer likeComment(String movementId) {
        //1.查询该用户是否已对该动态点赞，从comment表中,需要参数:动态id,当前用户id,CommentType类型
        Boolean hasComment=commentApi.hasComment(movementId,UserHolder.getUserId(),CommentType.LIKE);
        //2.如果已经点赞，抛出自定义异常
        if (hasComment){
            throw new BusinessException(ErrorResult.likeError());
        }
        //3.之前没点过赞则调用api保持数据到mongodb的comment表中CommentType.like
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));//点赞的动态id
        comment.setCommentType(CommentType.LIKE.getType());//类型
        comment.setUserId(UserHolder.getUserId());//评论人
        comment.setCreated(System.currentTimeMillis());
        //4.被publishUserId评论人的id在api层封装，返回最新的点赞
        Integer count = commentApi.save(comment);
        //5.拼接redis的key,将用户的点赞状态存入到redis中,采用hash，key  value(hashKey value)
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;//唯一标识该动态，在该动态下可以有很多用户点赞
        String hashKey = Constants.MOVEMENT_LIKE_HASHKEY + UserHolder.getUserId();//唯一标识该动态点赞的用户
        redisTemplate.opsForHash().put(key,hashKey,"1");
        return count;

    }

    /**
     * 取消点赞
     * @param movementId
     * @return
     */
    public Integer dislikeComment(String movementId) {
        //1.查询该用户是否已对该动态点赞，从comment表中,需要参数:动态id,当前用户id,CommentType类型
        Boolean hasComment=commentApi.hasComment(movementId,UserHolder.getUserId(),CommentType.LIKE);
        //2.如果没有点赞，抛出自定义异常
        if (!hasComment){
            throw new BusinessException(ErrorResult.disLikeError());
        }
        //3.之前点过赞则调用api删除comment表中的数据,构造comment对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));//点赞的动态id
        comment.setCommentType(CommentType.LIKE.getType());//类型
        comment.setUserId(UserHolder.getUserId());//当前操作的人

        //4.被publishUserI动态作者的id在api层封装，返回最新的点赞数量
        Integer count = commentApi.delete(comment);
        //5.拼接redis的key,将用户的点赞状态从redis中删除,采用hash，key  value(hashKey value)
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;//唯一标识该动态，在该动态下可以有很多用户点赞
        String hashKey = Constants.MOVEMENT_LIKE_HASHKEY + UserHolder.getUserId();//唯一标识该动态点赞的用户
        redisTemplate.opsForHash().delete(key,hashKey);
        return count;
    }

    /**
     * 喜欢
     * @param movementId
     * @return
     */
    public Integer loveComment(String movementId) {
        //1.查询该用户是否已对该动态喜欢，从comment表中,需要参数:动态id,当前用户id,CommentType类型
        Boolean hasComment=commentApi.hasComment(movementId,UserHolder.getUserId(),CommentType.LOVE);
        //2.如果已经喜欢，抛出自定义异常
        if (hasComment){
            throw new BusinessException(ErrorResult.loveError());
        }
        //3.之前没点喜欢则调用api保持数据到mongodb的comment表中CommentType.like
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));//喜欢的动态id
        comment.setCommentType(CommentType.LOVE.getType());//类型
        comment.setUserId(UserHolder.getUserId());//评论人
        comment.setCreated(System.currentTimeMillis());
        //4.被publishUserId评论人的id在api层封装，返回最新的喜欢
        Integer count = commentApi.save(comment);
        //5.拼接redis的key,将用户的喜欢状态存入到redis中,采用hash，key  value(hashKey value)
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;//唯一标识该动态，在该动态下可以有很多用户喜欢
        String hashKey = Constants.MOVEMENT_LOVE_HASHKEY + UserHolder.getUserId();//唯一标识该动态喜欢的用户
        redisTemplate.opsForHash().put(key,hashKey,"1");
        return count;

    }

    /**
     * 取消喜欢
     * @param movementId
     * @return
     */
    public Integer unloveComment(String movementId) {
        //1.查询该用户是否已对该动态喜欢，从comment表中,需要参数:动态id,当前用户id,CommentType类型
        Boolean hasComment=commentApi.hasComment(movementId,UserHolder.getUserId(),CommentType.LOVE);
        //2.如果没有喜欢，抛出自定义异常
        if (!hasComment){
            throw new BusinessException(ErrorResult.disloveError());
        }
        //3.之前点过喜欢则调用api删除comment表中的数据,构造comment对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));//喜欢的动态id
        comment.setCommentType(CommentType.LOVE.getType());//类型
        comment.setUserId(UserHolder.getUserId());//当前操作的人

        //4.被publishUserI动态作者的id在api层封装，返回最新的喜欢数量
        Integer count = commentApi.delete(comment);
        //5.拼接redis的key,将用户的点赞状态从redis中删除,采用hash，key  value(hashKey value)
        String key = Constants.MOVEMENTS_INTERACT_KEY + movementId;//唯一标识该动态，在该动态下可以有很多用户喜欢
        String hashKey = Constants.MOVEMENT_LOVE_HASHKEY + UserHolder.getUserId();//唯一标识该动态喜欢的用户
        redisTemplate.opsForHash().delete(key,hashKey);
        return count;
    }

    /**
     * 对动态的评论进行点赞
     * @param commentId
     * @return
     */
    public Integer likeMovementComment(String commentId) {
        //1.首先啊查寻该用户是否对该评论进行点赞，如果点赞则抛出自定义异常，如果没有点赞才保持点赞状态
        //操作comment表,根据评论id,当前用户id,点赞类型唯一确定一条点赞数据
        Boolean commentLike = commentApi.hasComment(commentId, UserHolder.getUserId(), CommentType.LIKE);
        //2.如果已经喜欢，抛出自定义异常
        if (commentLike){
            throw new BusinessException(ErrorResult.likeError());
        }
        //3.之前没点过赞则调用api保持数据到mongodb的comment表中CommentType.like
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(commentId));//动态下的评论id
        comment.setCommentType(CommentType.LIKE.getType());//类型
        comment.setUserId(UserHolder.getUserId());//当前点赞该评论的人
        comment.setCreated(System.currentTimeMillis());
        //4.返回最新的点赞
        Integer count = commentApi.saveComment(comment);
        //5.拼接redis的key,将用户的点赞状态存入到redis中,采用hash，key  value(hashKey value)
        String key = Constants.MOVEMENTS_INTERACT_KEY + commentId;//唯一标识该动态，在该动态下可以有很多用户点赞
        String hashKey = Constants.MOVEMENT_LIKE_HASHKEY + UserHolder.getUserId();//唯一标识该动态点赞的用户
        redisTemplate.opsForHash().put(key,hashKey,"1");
        return count;
    }

    /**
     * 取消评论点赞
     * @param commentId
     * @return
     */
    public Integer dislikeMovementComment(String commentId) {
        //1.查询该用户是否已对该评论点赞，从comment表中,需要参数:评论id,当前用户id,CommentType类型
        Boolean hasComment=commentApi.hasComment(commentId,UserHolder.getUserId(),CommentType.LIKE);
        //2.如果没有点赞，抛出自定义异常
        if (!hasComment){
            throw new BusinessException(ErrorResult.disLikeError());
        }
        //3.之前点过赞则调用api删除comment表中commentId评论点赞的数据,构造comment对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(commentId));//点赞的评论id
        comment.setCommentType(CommentType.LIKE.getType());//类型
        comment.setUserId(UserHolder.getUserId());//当前操作的人

        //4.删除该用户对commentId评论的点赞，并对之前评论点赞的数量进行修改，返回最新的点赞数量
        Integer count = commentApi.deleteComment(comment);
        //5.拼接redis的key,将用户的点赞状态从redis中删除,采用hash，key  value(hashKey value)
        String key = Constants.MOVEMENTS_INTERACT_KEY + commentId;//唯一标识该动态，在该动态下可以有很多用户点赞
        String hashKey = Constants.MOVEMENT_LIKE_HASHKEY + UserHolder.getUserId();//唯一标识该动态点赞的用户
        redisTemplate.opsForHash().delete(key,hashKey);
        return count;
    }
}
