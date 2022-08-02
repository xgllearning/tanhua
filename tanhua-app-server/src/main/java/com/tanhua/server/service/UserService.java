package com.tanhua.server.service;

import com.tanhua.autoconfig.template.SmsTemplate;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 只在当前工程有用,所以没必要创建接口
 */
@Service
public class UserService {

    @Autowired
    private SmsTemplate template;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    /**
     * 发送短信验证码
     * @param phone
     */
    public ResponseEntity sendMsg(String phone) {
        //1.随机生成6位数字
        String code = RandomStringUtils.randomNumeric(6);
        //2.调用发送短信模板template对象，发送手机短信
        template.sendSms(phone,code);
        //3.将验证码存入redis中
        redisTemplate.opsForValue().set("CHECK_CODE_"+phone,code, Duration.ofMinutes(5));//验证码失效时间
        //4、构建返回值
        //ok(),里面放之前的返回内容，正常返回状态码200，可以通过status设置
        //return ResponseEntity.status(500).body("出错了");
        return ResponseEntity.ok(null);
    }
}
