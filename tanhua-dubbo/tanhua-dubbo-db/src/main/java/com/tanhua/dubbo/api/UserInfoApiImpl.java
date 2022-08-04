package com.tanhua.dubbo.api;

import com.tanhua.dubbo.mappers.UserInfoMapper;
import com.tanhua.model.domain.UserInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

//dubbo服务提供者
@DubboService
public class UserInfoApiImpl implements UserInfoApi {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public void save(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    @Override
    public void update(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }

    /**
     * 根据ID查询个人资料(详细信息)
     * @param userID
     * @return
     */
    @Override
    public UserInfo findById(Long userID) {

        return userInfoMapper.selectById(userID);
    }
}