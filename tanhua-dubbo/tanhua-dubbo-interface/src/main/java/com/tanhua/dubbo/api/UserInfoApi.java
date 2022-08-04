package com.tanhua.dubbo.api;

import com.tanhua.model.domain.UserInfo;

public interface UserInfoApi {

    //保存用户详情信息
    public void save(UserInfo userInfo);

    //更新用户详细信息
    public void update(UserInfo userInfo);
    //根据ID查询个人资料(详细信息)
    UserInfo findById(Long userID);
}
