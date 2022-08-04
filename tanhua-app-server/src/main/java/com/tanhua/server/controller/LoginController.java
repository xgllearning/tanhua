package com.tanhua.server.controller;

import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

        try {
            //1.获取map中的请求参数
            String phone = (String) map.get("phone");
            String code = (String) map.get("verificationCode");
            //2.调用userService完成用户登录
            Map retMap = userService.loginVerification(phone,code);
            return ResponseEntity.ok(retMap);
        }catch (BusinessException be){//先捕获自定义异常
            ////TODO：这些都是可以预知的异常信息
            ErrorResult errorResult = be.getErrorResult();
            //springboot内部也提供枚举的方式返回状态码
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }catch (Exception e) {//如果不是自定义异常，捕获大的，可能就是空指针等异常...
            //TODO：这些都是不可预知的异常信息
            e.printStackTrace();
            //springboot内部也提供枚举的方式返回状态码
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResult.error());
        }

    }
}
