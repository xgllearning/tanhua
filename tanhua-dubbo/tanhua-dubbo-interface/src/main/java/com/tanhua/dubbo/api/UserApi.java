package com.tanhua.dubbo.api;

import com.tanhua.model.domain.User;

public interface UserApi {
    //根据手机号查询用户
    public User findByMobile(String mobile);
}
