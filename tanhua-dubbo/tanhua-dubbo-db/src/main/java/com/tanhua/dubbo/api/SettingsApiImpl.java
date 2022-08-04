package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.dubbo.mappers.SettingsMapper;
import com.tanhua.model.domain.Settings;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class SettingsApiImpl implements SettingsApi{

    @Autowired
    private SettingsMapper settingsMapper;

    //根据用户id查询
    public Settings findByUserId(Long userId) {
        LambdaQueryWrapper<Settings> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Settings::getUserId,userId);
        return settingsMapper.selectOne(queryWrapper);
    }
}
