package com.tanhua.server.service;

import com.tanhua.dubbo.api.QuestionApi;
import com.tanhua.dubbo.api.SettingsApi;
import com.tanhua.model.domain.Question;
import com.tanhua.model.domain.Settings;
import com.tanhua.model.vo.SettingsVo;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SettingsService {
    //调用QuestionApi进行查询陌生人问题
    @DubboReference
    private QuestionApi questionApi;
    //调用SettingsApi进行查询通知设置
    @DubboReference
    private SettingsApi settingsApi;

    public SettingsVo settings() {
        //TODO:目的是封装settingsVo返回
        SettingsVo settingsVo = new SettingsVo();
        //1.获取用户id
        Long userId = UserHolder.getUserId();
        settingsVo.setId(userId);
        //2.获取当前用户手机号
        settingsVo.setPhone(UserHolder.getMobile());
        //3.通过questionApi查询陌生人问题，根据userId
        Question question = questionApi.findByUserId(userId);
        String txt = question == null ? "你喜欢java吗？" : question.getTxt();
        settingsVo.setStrangerQuestion(txt);
        //4.通过settingsApi查询通知设置，根据userId
        Settings settings=settingsApi.findByUserId(userId);
        if (settings!=null){
            settingsVo.setGonggaoNotification(settings.getGonggaoNotification());
            settingsVo.setPinglunNotification(settings.getPinglunNotification());
            settingsVo.setLikeNotification(settings.getLikeNotification());
        }

        return settingsVo;
    }
}
