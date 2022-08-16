package com.tanhua.admin.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VideoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Video;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ManagerService {

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private VideoApi videoApi;

    /**
     * 分页查询用户信息
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult findAllUsers(Integer page, Integer pagesize) {


        Page<UserInfo> myPage=userInfoApi.findAll(page,pagesize);
        List<UserInfo> userInfos = myPage.getRecords();
        if (CollUtil.isEmpty(userInfos)){
            return new PageResult();
        }
        return new PageResult(page,pagesize, Math.toIntExact(myPage.getTotal()),userInfos);
    }


}
