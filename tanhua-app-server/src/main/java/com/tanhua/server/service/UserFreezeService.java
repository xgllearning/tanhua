package com.tanhua.server.service;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSON;
import com.tanhua.commons.utils.Constants;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserFreezeService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    //构造方法，探花系统在用户登录，评论，发布动态时判断其冻结状态，如果被冻结抛出异常
    public void checkUserStatus(Integer state,Long userId) {
        //拼接redisKey
        String redisKey = Constants.USER_FREEZE+userId;
        //获取redis中的冻结信息
        String value = redisTemplate.opsForValue().get(redisKey);
        if (!StringUtils.isEmpty(value)){//判断是否存在冻结
            //存在冻结的话转为map
            Map map = JSON.parseObject(value, Map.class);
            Integer freezingRange = Convert.convert(Integer.class, map.get("freezingRange"));//1登录 2发言  3发布动态
            if(freezingRange == state) {
                throw new BusinessException(ErrorResult.builder().errMessage("您的账号被冻结！").build());
            }
        }

    }
}
