package com.tanhua.server.service;

import com.tanhua.autoconfig.template.SmsTemplate;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.model.domain.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 只在当前工程有用,所以没必要创建接口
 */
@Service
public class UserService {

    @Autowired
    private SmsTemplate template;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @DubboReference
    private UserApi userApi;
    /**
     * 发送短信验证码
     * @param phone
     */
    public ResponseEntity sendMsg(String phone) {
        //1.随机生成6位数字
        //String code = RandomStringUtils.randomNumeric(6);
        String code="123456";
        //2.调用发送短信模板template对象，发送手机短信
        //template.sendSms(phone,code);
        //3.将验证码存入redis中
        redisTemplate.opsForValue().set("CHECK_CODE_"+phone,code, Duration.ofMinutes(5));//验证码失效时间
        //4、构建返回值
        //ok(),里面放之前的返回内容，正常返回状态码200，可以通过status设置
        //return ResponseEntity.status(500).body("出错了");
        return ResponseEntity.ok(null);
    }

    /**
     * 用户登录
     * @param phone
     * @param code
     * @return
     */
    public Map loginVerification(String phone, String code) {
        //1.从redis中获取验证码
        String redisCode = redisTemplate.opsForValue().get("CHECK_CODE_" + phone);
        //2.对验证码进行校验,redis中验证码是否存在和是否与输入的一致
        if (StringUtils.isEmpty(redisCode)||!StringUtils.equals(redisCode,code)){
            //验证码无效
            throw  new RuntimeException("验证码错误");
        }
        //3.验证码通过后，删除redis中的验证码
        redisTemplate.delete("CHECK_CODE_" + phone);
        //4.通过手机号码查询用户,DubboReference引用UserApi接口
        User user = userApi.findByMobile(phone);
        //5.判断用户存不存在,如果不存在需要保存到数据库,如果是新用户，还需要返回isNew=true
        boolean isNew = false;
        if (Objects.isNull(user)){
            user = new User();
            user.setMobile(phone);
            user.setCreated(new Date());
            user.setUpdated(new Date());
            user.setPassword(DigestUtils.md5Hex("123456"));
            Long userId = userApi.save(user);
            //进入则为新用户,将isNew设为true
            isNew = true;
        }
        //6.通过JWT工具类生成token(根据id和phone生成)
        HashMap tokenMap = new HashMap<>();
        tokenMap.put("id",user.getId());
        tokenMap.put("phone",phone);
        String token = JwtUtils.getToken(tokenMap);
        //7.构造返回值,需要token、isNew
        Map retMap = new HashMap<>();
        retMap.put("token",token);
        retMap.put("isNew",isNew);
        return retMap;
    }
}
