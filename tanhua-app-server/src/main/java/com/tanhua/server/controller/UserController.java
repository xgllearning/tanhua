package com.tanhua.server.controller;

import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.server.service.UserInfoService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserInfoService userInfoService;

    /**完善用户信息
     * 接口路径	/user/loginReginfo
     * 请求方式	POST
     * 请求header	Authorization
     * Body参数	UserInfo
     * 响应结果	ResponseEntity
     */
    @PostMapping("/loginReginfo")
    public ResponseEntity loginReginfo(@RequestBody UserInfo userInfo, @RequestHeader("Authorization") String token){
        //1.验证token是否合法，通过JwtUtils
        boolean verifyToken = JwtUtils.verifyToken(token);
        if (!verifyToken){//token不合法
            return ResponseEntity.status(401).body(null);
        }
        //2.token合法则解析token，并拿到token中的用户id
        Claims claims = JwtUtils.getClaims(token);
        //String类型转Integer
        Integer id = (Integer) claims.get("id");
        //Integer转Long
        userInfo.setId(Long.valueOf(id));
        //3、调用service
        userInfoService.save(userInfo);
        return ResponseEntity.ok(null);

    }




}
