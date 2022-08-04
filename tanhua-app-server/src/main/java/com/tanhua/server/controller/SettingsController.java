package com.tanhua.server.controller;

import com.tanhua.model.vo.SettingsVo;
import com.tanhua.server.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class SettingsController {
    //注入SettingsService处理业务逻辑
    @Autowired
    private SettingsService settingsService;

    /**查询通用设置
     * 接口路径	/users/settings
     * 请求方式	GET
     * 请求头	Authorization
     * 响应结果	ResponseEntity<SettingsVO>
     */
    @GetMapping("/settings")
    public ResponseEntity settings(){
        //1.根据SettingsService查询通用设置(陌生人问题、手机号、通知设置)
        SettingsVo settingsVo=settingsService.settings();
        return ResponseEntity.ok(settingsVo);
    }


    /**设置陌生人问题
     * 接口路径	/users/questions
     * 请求方式	POST
     * 参数	QuestionDto
     * 请求头	Authorization
     * 响应结果	ResponseResult
     */
    @PostMapping("/questions")
    public ResponseEntity questions(@RequestBody Map map){
        //获取参数,保存问题settingsService
        String content = (String) map.get("content");
        settingsService.saveQuestion(content);
        return ResponseEntity.ok(null);
    }

    /**通知设置
     * 接口路径	/users/notifications/setting
     * 请求方式	POST
     * 参数	Settings
     * 响应结果	ResponseEntity<void>
     */
    @PostMapping("/notifications/setting")
    public ResponseEntity notifications(@RequestBody Map map){

        //获取参数放到service层处理
        settingsService.saveSettings(map);
        return ResponseEntity.ok(null);
    }
}
