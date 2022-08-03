package com.tanhua.server.controller;

import com.tanhua.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private UserService userService;

    /**发送短信验证码
     * 接口路径	/user/login
     * 请求方式	POST
     * Body参数	phone (Map)
     * 响应结果	ResponseEntity<Void>
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map map){
        String phone = (String) map.get("phone");
        //调用userService发送短信方法
        return userService.sendMsg(phone);
    }

    /**用户登录
     * 接口路径	/user/loginVerification
     * 请求方式	POST
     * Body参数	phone，verificationCode
     * 响应结果	ResponseEntity
     */
    @PostMapping("/loginVerification")
    public ResponseEntity loginVerification(@RequestBody Map map){
        //1.获取map中的请求参数
        String phone = (String) map.get("phone");
        String code = (String) map.get("loginVerification");
        //2.调用userService完成用户登录
        Map retMap = userService.loginVerification(phone,code);
        return ResponseEntity.ok(retMap);
    }
}
