package com.tanhua.server.service;

import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.autoconfig.template.SmsTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.model.domain.User;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
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

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @DubboReference
    private UserApi userApi;

    @Autowired
    private UserFreezeService userFreezeService;
    /**
     * 发送短信验证码
     * @param phone
     */
    public ResponseEntity sendMsg(String phone) {
        //TODO：优化冻结，首先查询手机号是否存在，如果存在则查看是否冻结，如果是新用户则不需要查看冻结状态
        User user = userApi.findByMobile(phone);
        if (user!=null){
            userFreezeService.checkUserStatus(1,user.getId());
        }

        //1.随机生成6位数字
        //String code = RandomStringUtils.randomNumeric(6);
        String code="123456";
        //2.调用发送短信模板template对象，发送手机短信
        //template.sendSms(phone,code);
        //3.将验证码存入redis中
        redisTemplate.opsForValue().set("CHECK_CODE_"+phone,code, Duration.ofMinutes(10));//验证码失效时间
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
            //验证码无效,抛出自定义异常(可预知的错误，如图片不合法，验证码错误等等。这类错误也可以理解为业务异常，可以通过自定义异常类来处理；)
            throw  new BusinessException(ErrorResult.loginError());
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
//            user.setCreated(new Date());
//            user.setUpdated(new Date());
            user.setPassword(DigestUtils.md5Hex("123456"));

            Long userId = userApi.save(user);
            user.setId(userId);
            //进入则为新用户,将isNew设为true
            isNew = true;

            //当用户保存成功后，可以注册环信用户
            //构造环信的用户名和密码，用户名需要字母和数字
            String hxUser = "hx"+user.getId();
            Boolean createUser = huanXinTemplate.createUser(hxUser, Constants.INIT_PASSWORD);
            //判断环信用户是否注册成功，如果注册成功，将环信信息更新到数据库，即可与用户绑定在一起
            if (createUser){
                user.setHxUser(hxUser);
                user.setHxPassword(Constants.INIT_PASSWORD);
                userApi.update(user);
            }

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
