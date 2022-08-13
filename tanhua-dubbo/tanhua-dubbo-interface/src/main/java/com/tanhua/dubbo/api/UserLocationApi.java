package com.tanhua.dubbo.api;

import java.util.List;

public interface UserLocationApi {
    //更新地理位置
    Boolean updateLocation(Long userId, Double longitude, Double latitude, String address);
    //查询user_Location表，根据当前用户id查询出在范围内的所有用户id,此时包含当前用户id
    List<Long> queryNearUser(Long userId, String gender, String distance);
}
