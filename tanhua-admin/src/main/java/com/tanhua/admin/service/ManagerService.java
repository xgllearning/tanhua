package com.tanhua.admin.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VideoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.Video;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ManagerService {

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private VideoApi videoApi;

    @DubboReference
    private MovementApi movementApi;

    /**
     * 分页查询用户信息
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult findAllUsers(Integer page, Integer pagesize) {


        Page<UserInfo> myPage=userInfoApi.findAll(page,pagesize);
        List<UserInfo> userInfos = myPage.getRecords();
        if (CollUtil.isEmpty(userInfos)){
            return new PageResult();
        }
        return new PageResult(page,pagesize, Math.toIntExact(myPage.getTotal()),userInfos);
    }

    /**
     * 查询用户详情
     * @param userId
     * @return
     */
    public UserInfo findUserById(Long userId) {
        UserInfo userInfo = userInfoApi.findById(userId);
        return userInfo;
    }

    /**
     * 分页查询视频列表
     * @param page
     * @param pagesize
     * @param uid
     * @return
     */
    public PageResult findAllVideos(Integer page, Integer pagesize, Long uid) {
        //根据uid分页查询
        List<Video> videos = videoApi.findByUserId(page,pagesize,uid);

        if (CollUtil.isEmpty(videos)){
            return new PageResult();
        }

        int count = videos.size();
        return new PageResult(page,pagesize,count,videos);
    }

    /**
     * 在用户管理(根据用户id)和动态审核中(根据状态，查询全部、查询审核过的、查询未审核过的、查询审核失败的)都需要使用
     * @param page
     * @param pagesize
     * @param uid
     * @param state
     * @return
     */
    public PageResult findAllMovements(Integer page, Integer pagesize, Long uid, Integer state) {
        //需要的是movementVo,MovementsVo init(UserInfo userInfo, Movement item)
        //1.查询的是movement表,返回的PageResult对象，里面携带的是list<Movement>
        PageResult pageResult=movementApi.findByUidOrState(page,pagesize,uid,state);
        //2.获取pageResult集合
        List<Movement> items = (List<Movement>) pageResult.getItems();
        //3.判断是否为空，为空的话直接返回
        if (CollUtil.isEmpty(items)){
            return pageResult;
        }
        //4.不为空则封装MovementsVo，先获取每个动态的用户id封装集合
        List<Long> userIds = CollUtil.getFieldValues(items, "userId", Long.class);
        //5.根据id查询用户详情
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        //5.每一个movement对象就需要封装成一个MovementsVo
        List<MovementsVo> vos = new ArrayList<>();
        for (Movement movement : items) {
            //根据movement中的用户id查询用户详情
            UserInfo userInfo = map.get(movement.getUserId());
            if (userInfo!=null){
                MovementsVo vo = MovementsVo.init(userInfo, movement);
                vos.add(vo);
            }
        }
        //封装pageResult对象返回
        pageResult.setItems(vos);
        return pageResult;
    }
}
