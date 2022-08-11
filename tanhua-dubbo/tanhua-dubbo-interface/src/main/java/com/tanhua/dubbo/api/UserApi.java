package com.tanhua.dubbo.api;

import com.tanhua.model.domain.User;

public interface UserApi {
    //根据手机号查询用户
    public User findByMobile(String mobile);
    //保存用户
    Long save(User user);
    //修改手机号 - 3 保存
    void update(Long userId, String mobile);
    //更新用户信息
    void update(User user);
    //通过id查询用户
    User findById(Long userId);

    //根据环信ID查询用户详细信息
    User findByHuanxin(String huanxinId);
}
