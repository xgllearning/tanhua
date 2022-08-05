package com.tanhua.server.service;


import com.tanhua.dubbo.api.UserApi;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

    @DubboReference
    private UserApi userApi;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 修改手机号 - 2 校验验证码
     * @param code
     */
    public Boolean checkVerificationCode(String code) {
        //1.获取当前用户的手机号
        String phone = UserHolder.getMobile();
        //2.从redis中获取发送的验证码，与传过来的参数code进行比较
        String redisCode = redisTemplate.opsForValue().get("CHECK_CODE_" + phone);
        //String redisCode = (String) redisTemplate.opsForValue().get("CHECK_CODE_" + phone);
        //3.对验证码进行验证,如果过期或者不匹配则抛出自定义异常
        if (StringUtils.isEmpty(redisCode)||!StringUtils.equals(redisCode,code)){
            throw new BusinessException(ErrorResult.loginError());
        }
        //4.验证码通过后，删除redis中的code
        redisTemplate.delete("CHECK_CODE_" + phone);
        return true;
    }

    /**
     * 修改手机号 - 3 保存
     * @param mobile
     */
    public void update(String mobile) {
        //调用userApi执行修改手机号操作-根据用户id
        Long userId = UserHolder.getUserId();
        userApi.update(userId,mobile);
    }
}
