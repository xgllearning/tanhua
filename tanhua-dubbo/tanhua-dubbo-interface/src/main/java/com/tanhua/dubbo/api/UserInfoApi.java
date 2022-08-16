package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.model.domain.UserInfo;

import java.util.List;
import java.util.Map;

public interface UserInfoApi {

    //保存用户详情信息
    public void save(UserInfo userInfo);

    //更新用户详细信息
    public void update(UserInfo userInfo);
    //根据ID查询个人资料(详细信息)
    UserInfo findById(Long userID);
    /**
     * 批量查询用户详情
     *    返回值：Map<id,UserInfo>。key为用户id,userInfo为详情对象
     参数：推荐的用户id，以及查询条件
     */
    Map<Long, UserInfo> findByIds(List<Long> userIds, UserInfo info);

    /**
     * 分页查询
     * @param page
     * @param pagesize
     * @return
     */
    Page<UserInfo> findAll(Integer page, Integer pagesize);
}
