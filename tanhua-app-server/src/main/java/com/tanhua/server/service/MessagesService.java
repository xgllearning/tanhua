package com.tanhua.server.service;

import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.FriendApi;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.User;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessagesService {

    @DubboReference
    private UserApi userApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private FriendApi friendApi;

    @Autowired
    private HuanXinTemplate huanXinTemplate;


    /**
     * 根据环信ID查询用户详细信息
     * @param huanxinId
     * @return
     */
    public UserInfoVo findUserInfoByHuanxin(String huanxinId) {
        User user = userApi.findByHuanxin(huanxinId);
        //2、根据用户id查询用户详情
        UserInfo userInfo = userInfoApi.findById(user.getId());
        UserInfoVo vo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo,vo); //copy同名同类型的属性
        if(userInfo.getAge() != null) {
            vo.setAge(userInfo.getAge().toString());
        }
        return vo;



    }

    /**
     * 添加好友
     * @param friendId
     */
    public void contacts(Long friendId) {
        //1.获取当前用户id
        Long userId = UserHolder.getUserId();
        //2.通过huanXinTemplate注册好友关系到环信，根据环信名称进行添加,参数一当前用户，参数二好友用户
        Boolean add = huanXinTemplate.addContact(Constants.HX_USER_PREFIX + userId, Constants.HX_USER_PREFIX + friendId);
        //3.如果返回true，则证明添加好友关系成功，否则抛出异常
        if (!add){
            throw new BusinessException(ErrorResult.error());
        }
        //4.如果注册成功，则将好友关系记录到mongodb数据库中
        friendApi.save(userId,friendId);
    }
}
