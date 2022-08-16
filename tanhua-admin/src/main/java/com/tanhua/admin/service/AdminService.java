package com.tanhua.admin.service;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.admin.exception.BusinessException;
import com.tanhua.admin.mapper.AdminMapper;
import com.tanhua.commons.utils.Constants;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.Admin;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 管理员登录
     * @param map
     * @return
     */
    public Map login(Map map) {
        //1、解析携带的参数
        String username = (String) map.get("username");
        String password = (String) map.get("password");
        String verificationCode = (String) map.get("verificationCode");
        String uuid = (String) map.get("uuid");
        //2、判断用户名或者密码是否为空
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            //用户名或者密码为空
            throw new BusinessException("用户名或者密码为空");
//            Map map1 = new HashMap();
//            map.put("message","用户名或者密码为空");
//            return ResponseEntity.status(500).body(map1);
        }
        //3.拼接key,查询验证码是否正确
        String key = Constants.CAP_CODE+uuid;
        //4.查询redis
        String code = redisTemplate.opsForValue().get(key);
        //5.判断验证码是否正确，不正确或者不存在抛出异常
        if(StringUtils.isEmpty(code)||!StringUtils.equals(verificationCode,code)){
            throw new BusinessException("验证码错误");
        }

        //6.说明查询到验证码，且验证码一致，删除验证码
        redisTemplate.delete(key);
        //7.去数据库查询用户是否存在，并判断密码是否正确
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername,username);
        Admin admin = adminMapper.selectOne(queryWrapper);
        //8.调用工具类进行md5加密
        password = SecureUtil.md5(password);
        if (!StringUtils.equals(password,admin.getPassword())||admin==null){
            //用户名不存在或者密码不一致
            throw new BusinessException("用户名或者密码错误");
        }
        //9.用户正确，生成token返回，把数据封装进map携带回去
        Map<String, Object> claims = new HashMap<>();
        claims.put("id",admin.getId());
        claims.put("username",admin.getUsername());
        String token = JwtUtils.getToken(claims);
        //10.构造返回值
        Map<String, String> res = new HashMap<>();
        return res;
    }
}
