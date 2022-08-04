package com.tanhua.server.controller;

import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.UserInfoService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
//        //1.验证token是否合法，通过JwtUtils
//        boolean verifyToken = JwtUtils.verifyToken(token);
//        if (!verifyToken){//token不合法
//            return ResponseEntity.status(401).body(null);
//        }
//        //2.token合法则解析token，并拿到token中的用户id
//        Claims claims = JwtUtils.getClaims(token);
//        //String类型转Integer
//        Integer id = (Integer) claims.get("id");
        //从ThreadLocal获取用户id
        Long id = UserHolder.getUserId();
        //Integer转Long
        userInfo.setId(Long.valueOf(id));
        //3、调用service
        userInfoService.save(userInfo);
        return ResponseEntity.ok(null);

    }

    /**更新用户头像
     * 接口路径	/user/loginReginfo/head
     * 请求方式	POST
     * 请求header	Authorization
     * 请求参数	headPhoto
     * 响应结果	ResponseEntity
     */
    @PostMapping("/loginReginfo/head")
    public ResponseEntity updateUserInfo(MultipartFile headPhoto,@RequestHeader("Authorization") String token){
//        //1.验证token是否合法，通过JwtUtils
//        boolean verifyToken = JwtUtils.verifyToken(token);
//        if (!verifyToken){//token不合法
//            return ResponseEntity.status(401).body(null);
//        }
//        //2.token合法则解析token，并拿到token中的用户id
//        Claims claims = JwtUtils.getClaims(token);
//        //String类型转Integer
//        Integer id = (Integer) claims.get("id");
//        try {
            //从ThreadLocal获取用户id
            Long id = UserHolder.getUserId();
            //根据用户id更新上传的图片
            userInfoService.updateHead(headPhoto,id);
            return ResponseEntity.ok(null);
//        }catch (BusinessException be){//先捕获自定义异常
//            ////TODO：这些都是可以预知的异常信息
//            ErrorResult errorResult = be.getErrorResult();
//            //springboot内部也提供枚举的方式返回状态码
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
//        } catch (Exception e) {
//            //TODO：这些都是不可预知的异常信息
//            e.printStackTrace();
//            //springboot内部也提供枚举的方式返回状态码
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResult.error());
//        }

    }


}
