package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.*;
import com.tanhua.model.domain.Question;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TanhuaService {



    @DubboReference
    private RecommendUserApi recommendUserApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private QuestionApi questionApi;


    @Autowired
    private HuanXinTemplate template;

    @DubboReference
    private MovementApi movementApi;

    @DubboReference
    private UserLikeApi userLikeApi;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private MessagesService messagesService;

    @Value("${tanhua.default.recommend.users}")//注入配置信息
    private String recommendUser;
    /**
     * 查询当前用户今日佳人
     * @return
     */
    public TodayBest todayBest() {
        //1.获取当前登录用户的id
        Long userId = UserHolder.getUserId();
        //2.通过RecommendUserApi根据当前登录用户的id进行查询
        RecommendUser recommendUser = recommendUserApi.queryWithMaxScore(userId);
        //3.判断recommendUser是否为null
        if (Objects.isNull(recommendUser)){
            //3.1为null,设置默认数据
            recommendUser = new RecommendUser();
            recommendUser.setUserId(1l);
            recommendUser.setScore(99d);
        }
        //3.2不为null,将RecommendUser转化为TodayBest对象返回
        //需要先查询出来最佳佳人的详细信息，通过userInfoApi根据最佳佳人的id进行查询
        UserInfo userInfo = userInfoApi.findById(recommendUser.getUserId());
        TodayBest vo = TodayBest.init(userInfo, recommendUser);
        //返回
        return vo;
    }

    /**
     * 推荐好友列表
     * @param recommendUserDto
     * @return
     */
    public PageResult recommendation(RecommendUserDto recommendUserDto) {
        //1.获取当前登录用户的id
        Long userId = UserHolder.getUserId();
        //2.获取当前推荐列表的数据，从mongoDB中查询recommend_user表，根据用户id(toUserId)就可以查询出来推荐用户Id(userId)
        //调用recommendUserApi分页查询数据列表(其返回值为PageResult--封装的内容是recommendUser对象)
        PageResult pageResult=recommendUserApi.queryRecommendUserList(recommendUserDto.getPage(),recommendUserDto.getPagesize(),userId);
        //3.获取PageResult中的recommendUser数据列表list<>
        List<RecommendUser> items = (List<RecommendUser>) pageResult.getItems();
        //4.判断列表是否为空,为空直接返回
        if (items.size()==0){
            return pageResult;
        }
        //TODO：优化部分在UserInfoAPI中根据条件一次性查询所有用户列表详情，Service层进行数据筛选
        //  5.从items提取出所有的推荐用户id集合
        List<Long> ids = CollUtil.getFieldValues(items, "userId",Long.class);
        //  6.构建查询条件批量查询所有的用户详情，此时的条件都在recommendUserDto中，需要转为userInfo
        UserInfo userInfo = new UserInfo();
        userInfo.setAge(recommendUserDto.getAge());
        userInfo.setGender(recommendUserDto.getGender());
        Map<Long,UserInfo> map=userInfoApi.findByIds(ids,userInfo);
        //  7.循环推荐的数据列表，构建vo对象
        //返回的PageResult中的items列表数据应该是TodayBest对象,此时为RecommendUser，需要进行替换
        List<TodayBest> list = new ArrayList<>();
        for (RecommendUser item : items) {
            //items列表需要封装的对象是TodayBest,而不是RecommendUser,封装一个完整的TodayBest需要传入userInfo和RecommendUser
            //此时的userInfo应该是map中查询的userInfo，而不是自己封装的userInfo查询条件
            UserInfo info = map.get(item.getUserId());
            if (info!=null){
                TodayBest todayBest = TodayBest.init(info, item);
                list.add(todayBest);
            }
        }
        //替换pageResult中的items属性，RecommendUser-->TodayBest
        pageResult.setItems(list);
        //6.构造返回值
        return pageResult;
    }


//    public PageResult recommendation(RecommendUserDto recommendUserDto) {
//        //1.获取当前登录用户的id
//        Long userId = UserHolder.getUserId();
//        //2.获取当前推荐列表的数据，从mongoDB中查询recommend_user表，根据用户id(toUserId)就可以查询出来推荐用户Id(userId)
//        //调用recommendUserApi分页查询数据列表(其返回值为PageResult--封装的内容是recommendUser对象)
//        PageResult pageResult=recommendUserApi.queryRecommendUserList(recommendUserDto.getPage(),recommendUserDto.getPagesize(),userId);
//        //3.获取PageResult中的recommendUser数据列表list<>
//        List<RecommendUser> items = (List<RecommendUser>) pageResult.getItems();
//        //4.判断列表是否为空,为空直接返回
//        if (Objects.isNull(items)){
//            return pageResult;
//        }
//        //TODO：优化部分在UserInfoAPI中根据条件一次性查询所有用户列表详情，Service层进行数据筛选
////        Map<Long,UserInfo> map=userInfoApi.findByIds(items,);
//        //5.不为空则遍历数据列表，根据推荐用户Id(userId)去userInfoApi查询用户详细信息
//        //5.1返回的PageResult中的items列表数据应该是TodayBest对象,此时为RecommendUser，需要进行替换
//        List<TodayBest> list = new ArrayList<>();
//        for (RecommendUser item : items) {
//            Long recommendUserId = item.getUserId();
//            UserInfo userInfo = userInfoApi.findById(recommendUserId);
//            //TODO：前台传递的Dto对象还会携带筛选条件，则在查询出来的userInfo进行条件判断，不符合条件的跳过
//            if (Objects.nonNull(userInfo)){//判断userInfo是否有数据,有数据再进行数据封装
//                //性别条件判断,条件不为空，且查询出来的条件与传过来的条件不一致，则跳过
//                if (!StringUtils.isEmpty(recommendUserDto.getGender()) && !StringUtils.equals(recommendUserDto.getGender(),userInfo.getGender())){
//                    continue;
//                }
//                if (recommendUserDto.getAge()!=null && recommendUserDto.getAge() < userInfo.getAge()){
//                    //查询出来的用户年龄大于筛选条件
//                    continue;
//                }
//            //items列表需要封装的对象是TodayBest,而不是RecommendUser,封装一个完整的TodayBest需要传入userInfo和RecommendUser
//                TodayBest todayBest = TodayBest.init(userInfo, item);
//                list.add(todayBest);
//            }
//        }
//        //替换pageResult中的items属性，RecommendUser-->TodayBest
//        pageResult.setItems(list);
//        //6.构造返回值
//        return pageResult;
//    }

    /**
     * 联系人管理-查询佳人详情信息
     */
    public TodayBest personalInfo(Long userId) {
        //1.目的是返回TodayBest,需要查询到用户详情表和mongoDB中的推荐表
        UserInfo userInfo = userInfoApi.findById(userId);
        //2.根据当前用户查看的用户id-userId,和当前用户id唯一确定recommend表数据
        RecommendUser recommendUser=recommendUserApi.queryByUserId(userId,UserHolder.getUserId());
        //3.拼装返回值
        TodayBest todayBest = TodayBest.init(userInfo, recommendUser);
        return todayBest;
    }

    /**
     * 查看陌生人问题
     * @param userId
     * @return
     */
    public String strangerQuestions(Long userId) {
        Question question = questionApi.findByUserId(userId);
        //如果question为null,则返回一个自定义数据

        return question==null?"添加我的理由！你喜欢我吗？":question.getTxt();
    }

    /**好友申请（回复陌生人问题）
     * 想办法给环信服务端发送的
     * {
     *      "userId":1,
     *  	"huanXinId":“hx1",
     *     "nickname":"黑马小妹",
     *     "strangerQuestion":"你喜欢去看蔚蓝的大海还是去爬巍峨的高山？",
     *     "reply":"我喜欢秋天的落叶，夏天的泉水，冬天的雪地，只要有你一切皆可~"
     *  }
     *  思路：可以放到一个对象或者map中，然后通过json转换的工具类转换为json发送即可
     *  userId：当前操作用户的id，huanXinId：操作人的环信用户
     * niciname：当前操作人昵称
     * 接口路径	/tanhua/strangerQuestions
     * 请求方式	POST
     * 参数	Map
     * 响应结果	ResponseEntity<void>
     */
    public void replyQuestions(Long toUserId, String reply) {
        //获取当前用户id--userId
        Long userId = UserHolder.getUserId();
        //组装环信id--huanXinId
        String huanXinId = Constants.HX_USER_PREFIX + userId;
        //根据用户id查询详细信息获取昵称-nickname
        UserInfo userInfo = userInfoApi.findById(userId);
        //通过map封装对象
        Map map = new HashMap<>();
        map.put("userId",userId);
        map.put("huanXinId", huanXinId);
        map.put("nickname",userInfo.getNickname());
        //调用上面的方法得到陌生人问题
        map.put("strangerQuestion",strangerQuestions(userId));
        map.put("reply",reply);
        //将map对象转为json发送到环信服务端
        String json = JSON.toJSONString(map);
        //2、调用template对象，发送消息，参数：接受方的环信id，2、消息
        Boolean sendMsg = template.sendMsg(Constants.HX_USER_PREFIX + toUserId, json);
        if(!sendMsg) {
            throw  new BusinessException(ErrorResult.error());
        }
    }

    /**
     * 查询推荐用户列表
     * @return
     */
    public List<TodayBest> queryCardsList() {
        //1.获取当前用户id
        Long userId = UserHolder.getUserId();
        //2.调用api查询推荐数据列表-(排除喜欢和不喜欢的用户，并设置显示数量)--
        //推荐表中当前用户id即toUserId,推荐用户id--userId，而like表中当前用户id代表的是userId，
        List<RecommendUser> users=recommendUserApi.queryCardsList(userId,10);
        //2.1如果数据不存在，则构造数据返回
        if(CollUtil.isEmpty(users)){
            users=new ArrayList<>();
            //构造推荐用户id数据
            String[] userIds = recommendUser.split(",");
            //遍历推荐用户id,封装到RecommendUser中
            for (String id : userIds) {
                RecommendUser recommendUser = new RecommendUser();
                recommendUser.setUserId(Convert.toLong(id));
                recommendUser.setToUserId(userId);
                recommendUser.setScore(RandomUtil.randomDouble(60, 90));
                users.add(recommendUser);
            }

        }
        //3.此时说明查询出推荐用户数据,需要查询推荐用户的详细信息,先CollUtil得到推荐用户的id
        List<Long> ids = CollUtil.getFieldValues(users, "userId", Long.class);
        Map<Long, UserInfo> map = userInfoApi.findByIds(ids, null);
        ArrayList<TodayBest> vos = new ArrayList<>();
        for (RecommendUser user : users) {
            UserInfo userInfo = map.get(user.getUserId());
            if (userInfo!=null){
                TodayBest vo = TodayBest.init(userInfo, user);
                vos.add(vo);
            }
        }

        return vos;
    }

    /**
     * 左滑右滑--喜欢处理
     * @param likeUserId
     */
    public void likeUser(Long likeUserId) {
        //1.调用userLikeApi保存喜欢数据，如果存在数据则更新，不存在则保存,根据当前用户id和喜欢用户的id唯一确定一条数据
        Boolean save=userLikeApi.saveOrUpdate(UserHolder.getUserId(),likeUserId,true);
        if (!save){//如果保存失败则抛出自定义异常
            throw new BusinessException(ErrorResult.error());
        }
        //2.操作成功，操作redis,写入喜欢的数据，删除不喜欢的数据(喜欢的集合和不喜欢的集合)，redis中有自己的处理机制，直接删除即可，不会报错，会返回删除了几条数据
        redisTemplate.opsForSet().remove(Constants.USER_NOT_LIKE_KEY+UserHolder.getUserId(),likeUserId.toString());//删除当前用户不喜欢该用户的redis缓存
        redisTemplate.opsForSet().add(Constants.USER_LIKE_KEY+UserHolder.getUserId(),likeUserId.toString());//保存该用户id为喜欢用户
        //3.判断是否是双向喜欢，查询redis中对方的喜欢集合中有没有该用户的id
        if (isLike(likeUserId,UserHolder.getUserId())){
            //说明双向喜欢，则添加好友
            messagesService.contacts(likeUserId);
        }

    }

    //定义一个公共方法，用户判断是不是喜欢,即判断在redis中，该用户的值中是否有喜欢的用户id
    public Boolean isLike(Long userId,Long likeUserId){
        String key = Constants.USER_LIKE_KEY+userId;
        //判断某个数据是否在集合中
        return redisTemplate.opsForSet().isMember(key,likeUserId.toString());
    }

    //不喜欢处理
    public void notLikeUser(Long likeUserId) {
        //1.调用userLikeApi保存不喜欢数据，如果存在数据则更新，不存在则保存,根据当前用户id和喜欢用户的id唯一确定一条数据
        Boolean save=userLikeApi.saveOrUpdate(UserHolder.getUserId(),likeUserId,false);
        if (!save){//如果保存失败则抛出自定义异常
            throw new BusinessException(ErrorResult.error());
        }
        //2.操作成功，操作redis,写入喜欢的数据，删除不喜欢的数据(喜欢的集合和不喜欢的集合)，redis中有自己的处理机制，直接删除即可，不会报错，会返回删除了几条数据
        redisTemplate.opsForSet().remove(Constants.USER_LIKE_KEY+UserHolder.getUserId(),likeUserId.toString());//删除当前用户不喜欢该用户的redis缓存
        redisTemplate.opsForSet().add(Constants.USER_NOT_LIKE_KEY+UserHolder.getUserId(),likeUserId.toString());//保存该用户id为喜欢用户
//        //3.判断是否是双向喜欢，查询redis中对方的喜欢集合中有没有该用户的id
//        if (isLike(likeUserId,UserHolder.getUserId())){
//            //说明双向喜欢，则添加好友
//            messagesService.contacts(likeUserId);
//        }
        //不喜欢则删除好友关系,删除环信好友关系,删除好友表中的数据，
        //template.deleteContact(Constants.HX_USER_PREFIX+UserHolder.getUserId(),Constants.HX_USER_PREFIX+likeUserId);
    }
}
