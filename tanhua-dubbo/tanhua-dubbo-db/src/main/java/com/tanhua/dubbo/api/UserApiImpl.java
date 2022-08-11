package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.tanhua.dubbo.mappers.UserMapper;
import com.tanhua.model.domain.User;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class UserApiImpl implements UserApi{

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByMobile(String mobile) {
        //Plus中的基本CRUD在BaseMapper中都已得到了实现，因此我们继承该接口以后可以直接使用
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(mobile),User::getMobile,mobile);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public Long save(User user) {
        userMapper.insert(user);
        return user.getId();
    }
    //修改手机号 - 3 保存
    @Override
    public void update(Long userId, String mobile) {
        User user = new User();
        user.setMobile(mobile);
        user.setId(userId);
        userMapper.updateById(user);
    }

    /**
     * 更新用户信息
     * @param user
     */
    @Override
    public void update(User user) {
        userMapper.updateById(user);//10.0.2.2:18080
    }

    /**
     * 通过id查询用户
     * @param userId
     * @return
     */
    @Override
    public User findById(Long userId) {
        User user = userMapper.selectById(userId);
        return user;
    }

}
