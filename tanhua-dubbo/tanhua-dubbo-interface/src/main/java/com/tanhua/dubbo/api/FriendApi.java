package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Friend;

import java.util.List;

public interface FriendApi {
    //添加好友
    void save(Long userId, Long friendId);
    //分页查询联系人列表
    List<Friend> findByUserId(Long userId, Integer page, Integer pagesize);
}
