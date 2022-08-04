package com.tanhua.server.controller;

import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.service.UserInfoService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UsersController {
    //查询详细信息，注入UserInfoService
    @Autowired
    private UserInfoService userInfoService;


    /**根据ID查询个人资料(详细信息)-参数userID： 用户id，当不传递时，查询当前用户的资料信息
     * 接口路径	/users
     * 请求方式	GET
     * 参数：userID、请求header	Authorization
     * 响应结果	UserInfo
     */
    @GetMapping
    public ResponseEntity users(@RequestHeader("Authorization") String token,Long userID){
//        //1.解析token是否合法
//        boolean verifyToken = JwtUtils.verifyToken(token);
//        if (!verifyToken){//不合法抛出异常
//            return ResponseEntity.status(401).body(null);
//        }
        //2. 获取token中的用户信息
        Claims claims = JwtUtils.getClaims(token);
        String id = claims.get("id").toString();
        //3.userID如果不为null,则根据userID进行查询，如果为null,则解析token，获取token携带的当前用户Id进行查询
        if (Objects.isNull(userID)){
            //为null,使用token携带的当前用户Id进行查询
            userID = Long.valueOf(id);
        }
        //4.根据userId进行查询
        UserInfoVo userInfo = userInfoService.findById(userID);
        return ResponseEntity.ok(userInfo);
    }


    /**更新个人资料
     * 接口路径	/users
     * 请求方式	PUT
     * 请求header	Authorization
     * 请求参数	UserInfo
     * 响应结果	ResponseEntity
     */
    @PutMapping
    public ResponseEntity users(@RequestHeader("Authorization") String token,@RequestBody UserInfo userInfo){
        //1.解析token是否合法
//        boolean verifyToken = JwtUtils.verifyToken(token);
//        if (!verifyToken){//不合法抛出异常
//            return ResponseEntity.status(401).body(null);
//        }
        //2. 获取token中的用户信息
        Claims claims = JwtUtils.getClaims(token);
        String id = claims.get("id").toString();
        //3.userInfo中不携带id，因此使用解析token的id进行更新
        userInfo.setId(Long.valueOf(id));
        //调用userInfoService更新用户资料
        userInfoService.update(userInfo);
        return ResponseEntity.ok(null);

    }
}
