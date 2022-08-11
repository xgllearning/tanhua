package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.QuestionApi;
import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
}
