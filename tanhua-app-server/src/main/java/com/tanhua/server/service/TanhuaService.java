package com.tanhua.server.service;

import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

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
}
