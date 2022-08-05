package com.tanhua.dubbo.api;

import com.tanhua.model.domain.User;

public interface UserApi {
    //根据手机号查询用户
    public User findByMobile(String mobile);
    //保存用户
    Long save(User user);
    //修改手机号 - 3 保存
    void update(Long userId, String mobile);
}
