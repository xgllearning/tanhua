package com.tanhua.server.service;

import com.tanhua.dubbo.api.QuestionApi;
import com.tanhua.dubbo.api.SettingsApi;
import com.tanhua.model.domain.Question;
import com.tanhua.model.domain.Settings;
import com.tanhua.model.vo.SettingsVo;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Map;
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

    /**
     * 设置陌生人问题
     * @param content
     */
    public void saveQuestion(String content) {
        //1.获取当前用户的id--UserHolder
        Long userId = UserHolder.getUserId();
        //2.调用questionApi，根据用户id查询当前用户的陌生人问题
        Question question = questionApi.findByUserId(userId);
        //3、判断问题是否存在
        if (Objects.isNull(question)){
            //3.1陌生人问题不存在则执行保存操作
            question = new Question();
            question.setUserId(userId);
            question.setTxt(content);
            questionApi.save(question);
        }else {
            //3.2 如果存在，更新
            question.setTxt(content);
            questionApi.update(question);
        }

    }

    /**
     * 通知设置
     * @param map
     */
    public void saveSettings(Map map) {
        //获取参数
        boolean likeNotification = (Boolean) map.get("likeNotification");
        boolean pinglunNotification = (Boolean) map.get("pinglunNotification");
        boolean gonggaoNotification = (Boolean)  map.get("gonggaoNotification");
        //获取当前用户id
        Long userId = UserHolder.getUserId();
        //根据当前用户id查询通知设置，调用settingsApi
        Settings settings = settingsApi.findByUserId(userId);
        //判断settings是否存在，存在则更新，不存在则插入
        if(settings == null) {
            //保存
            settings.setUserId(userId);
            settings.setPinglunNotification(pinglunNotification);
            settings.setLikeNotification(likeNotification);
            settings.setGonggaoNotification(gonggaoNotification);
            settingsApi.save(settings);
        }else {
            settings.setPinglunNotification(pinglunNotification);
            settings.setLikeNotification(likeNotification);
            settings.setGonggaoNotification(gonggaoNotification);
            settingsApi.update(settings);
        }
    }
}
