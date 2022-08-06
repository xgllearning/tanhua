package com.tanhua.server.service;

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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TanhuaService {

    @DubboReference
    private RecommendUserApi recommendUserApi;

    @DubboReference
    private UserInfoApi userInfoApi;

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
        if (Objects.isNull(items)){
            return pageResult;
        }
        //5.不为空则遍历数据列表，根据推荐用户Id(userId)去userInfoApi查询用户详细信息
        //5.1返回的PageResult中的items列表数据应该是TodayBest对象,此时为RecommendUser，需要进行替换
        List<TodayBest> list = new ArrayList<>();
        for (RecommendUser item : items) {
            Long recommendUserId = item.getUserId();
            UserInfo userInfo = userInfoApi.findById(recommendUserId);
            //TODO：前台传递的Dto对象还会携带筛选条件，则在查询出来的userInfo进行条件判断，不符合条件的跳过
            if (Objects.nonNull(userInfo)){//判断userInfo是否有数据,有数据再进行数据封装
                //性别条件判断,条件不为空，且查询出来的条件与传过来的条件不一致，则跳过
                if (!StringUtils.isEmpty(recommendUserDto.getGender()) && !StringUtils.equals(recommendUserDto.getGender(),userInfo.getGender())){
                    continue;
                }
                if (recommendUserDto.getAge()!=null && recommendUserDto.getAge() < userInfo.getAge()){
                    //查询出来的用户年龄大于筛选条件
                    continue;
                }
            //items列表需要封装的对象是TodayBest,而不是RecommendUser,封装一个完整的TodayBest需要传入userInfo和RecommendUser
                TodayBest todayBest = TodayBest.init(userInfo, item);
                list.add(todayBest);
            }
        }
        //替换pageResult中的items属性，RecommendUser-->TodayBest
        pageResult.setItems(list);
        //6.构造返回值
        return pageResult;
    }
}
