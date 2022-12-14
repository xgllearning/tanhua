package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.dubbo.mappers.BlackListMapper;
import com.tanhua.dubbo.mappers.UserInfoMapper;
import com.tanhua.model.domain.BlackList;
import com.tanhua.model.domain.UserInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class BlackListApiImpl implements BlackListApi{
    //注入blackListMapper
    @Autowired
    private BlackListMapper blackListMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;
    /**
     * 查询黑名单用户详细信息
     * @param userId
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<UserInfo> findByUserId(Long userId, int page, int size) {
        //1、构建分页参数对象Page,当前页和每页条数
        Page<UserInfo> userInfoPage = new Page<>(page,size);
        //2、调用方法分页（自定义编写 分页参数Page，sql条件参数）
        userInfoMapper.findBlackList(userInfoPage,userId);

        return userInfoPage;
    }

    /**
     * 移除黑名单用户--根据用户id和黑名单用户id
     * @param userId
     * @param blackUserId
     */
    @Override
    public void deleteBlackList(Long userId, Long blackUserId) {
        //构造查询条件
        LambdaQueryWrapper<BlackList> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BlackList::getUserId,userId);
        queryWrapper.eq(BlackList::getBlackUserId,blackUserId);
        blackListMapper.delete(queryWrapper);

    }
}
