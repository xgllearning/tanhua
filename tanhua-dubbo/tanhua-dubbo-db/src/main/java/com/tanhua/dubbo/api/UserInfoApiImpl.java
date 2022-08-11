package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tanhua.dubbo.mappers.UserInfoMapper;
import com.tanhua.model.domain.UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

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
    /**
     * 批量查询用户详情
     *    返回值：Map<id,UserInfo>。key为用户id,userInfo为详情对象
     参数：推荐的用户id，以及查询条件
     */
    @Override
    public Map<Long, UserInfo> findByIds(List<Long> userIds, UserInfo userInfo) {
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        //1.构造查询条件,根据id进行查询详情
        queryWrapper.in(UserInfo::getId,userIds);
        //2.根据条件进行筛选
        if (userInfo!=null){//此时说明有条件
            if (userInfo.getAge()!=null){//年龄小于参数age
                queryWrapper.lt(UserInfo::getAge,userInfo.getAge());
            }
            if(!StringUtils.isEmpty(userInfo.getGender())) {//根据性别
                queryWrapper.eq(UserInfo::getGender,userInfo.getGender());
            }
//            if(!StringUtils.isEmpty(userInfo.getNickname())) {//名字模糊搜索
//                queryWrapper.like(UserInfo::getNickname,userInfo.getNickname());
//            }
        }
        //查询返回所有符合条件的用户详情
        List<UserInfo> infoList = userInfoMapper.selectList(queryWrapper);
        //封装为Map<Long, UserInfo>对象返回,使用hutool工具类,将集合转为map，用其属性值当键
        Map<Long, UserInfo> map = CollUtil.fieldValueMap(infoList,"id");

        return map;
    }


}