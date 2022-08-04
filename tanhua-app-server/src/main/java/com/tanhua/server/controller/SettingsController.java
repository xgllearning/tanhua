package com.tanhua.server.controller;

import com.tanhua.model.vo.SettingsVo;
import com.tanhua.server.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
