package com.tanhua.server.service;

import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class UserInfoService {
    //注入UserInfoApi接口实现保存
    @DubboReference
    private UserInfoApi userInfoApi;

    /**完善用户信息
     */
    public void save(UserInfo userInfo) {
        userInfoApi.save(userInfo);
    }
}
