package com.tanhua.admin.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.tanhua.admin.interceptor.AdminHolder;
import com.tanhua.admin.service.AdminService;
import com.tanhua.commons.utils.Constants;
import com.tanhua.model.domain.Admin;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/system/users")
public class SystemController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**获取验证码
     * 接口路径	/system/users/verification
     * 请求方式	GET
     * 请求参数	uuid
     * 响应结果	验证码图片
     */
    @GetMapping("/verification")
    public void verification(String uuid,HttpServletResponse response) throws IOException {
        //1.通过hutool工具类生成图片验证码
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(299, 97);
        //2.获取验证码
        String code = lineCaptcha.getCode();
        //3.将验证码存入到redis中
        redisTemplate.opsForValue().set(Constants.CAP_CODE+uuid,code);
        //4.通过输出流响应回去
        lineCaptcha.write(response.getOutputStream());

    }

    /**管理员登录
     * 接口路径	/system/users/login
     * 请求方式	POST
     * 请求参数	Map
     * 响应结果	token
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map map) {
        Map retMap = adminService.login(map);
        return ResponseEntity.ok(retMap);
    }

}
