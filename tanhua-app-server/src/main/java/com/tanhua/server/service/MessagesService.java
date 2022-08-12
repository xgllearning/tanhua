package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.FriendApi;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.User;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Friend;
import com.tanhua.model.vo.ContactVo;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    /**
     * 分页查询联系人列表
     */
    public PageResult findFrends(Integer page, Integer pagesize, String keyword) {
        //1.需要返回--list<ContactVo>,需要查询出userInfo
        //2.获取当前用户id
        Long userId = UserHolder.getUserId();
        //3.根据当前用户id查询出当前用户的好友
        List<Friend> friends= friendApi.findByUserId(userId,page,pagesize);
        //4.判断是否查询出friends,如果没有则直接返回PageResult
        if (CollUtil.isEmpty(friends)){
            return new PageResult();
        }
        //5.根据好友id查询出好友的详细信息
        List<Long> friendsId = CollUtil.getFieldValues(friends, "friendId", Long.class);
        //6.根据好友id查询好友详细信息,并传入查询条件UserInfo
        UserInfo userInfo = new UserInfo();
        userInfo.setNickname(keyword);
        Map<Long, UserInfo> map = userInfoApi.findByIds(friendsId, userInfo);
        //7.构造list<ContactVo>对象,一个friend就是一个ContactVo
        List<ContactVo> contactVos = new ArrayList<>();
        for (Friend friend : friends) {
            UserInfo userInfo1 = map.get(friend.getFriendId());
            if (userInfo1!=null){
                ContactVo contactVo = ContactVo.init(userInfo1);
                contactVos.add(contactVo);
            }
        }
        return new PageResult(page,pagesize, Math.toIntExact(0l),contactVos);
    }
}
