package com.tanhua.dubbo.api;

public interface UserLocationApi {
    //更新地理位置
    Boolean updateLocation(Long userId, Double longitude, Double latitude, String address);
}
