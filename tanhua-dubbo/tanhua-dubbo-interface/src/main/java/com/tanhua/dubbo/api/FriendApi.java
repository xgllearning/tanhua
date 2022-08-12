package com.tanhua.dubbo.api;

public interface FriendApi {
    //添加好友
    void save(Long userId, Long friendId);
}
