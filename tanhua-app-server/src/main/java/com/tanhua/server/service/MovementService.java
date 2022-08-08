package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovementService {


    @Autowired
    private OssTemplate ossTemplate;

    @DubboReference
    private MovementApi movementApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 发布动态，imageContet-可以携带多张文件，采用数组
     *textContent文字动态
     *location位置longitude经度
     *latitude纬度
     *其余通过对象接收Movement
     * @param movement
     * @param imageContent
     */
    public void publishMovement(Movement movement, MultipartFile[] imageContent) throws IOException {
        //1.判断参数textContent文字动态是否为空，如果为空则抛出自定义异常
        if (StringUtils.isEmpty(movement.getTextContent())){
            throw new BusinessException(ErrorResult.contentError());
        }
        //2.获取当前用户id,保存内容到动态表,但Movement需要进行封装,pid、created、userId、medias
        Long userId = UserHolder.getUserId();
        ArrayList<String> medias = new ArrayList<>();
        //3.遍历imageContent，通过ossTemplate上传图片，进而获取url地址，封装进Movement
        for (MultipartFile multipartFile : imageContent) {
            String upload = ossTemplate.upload(multipartFile.getOriginalFilename(), multipartFile.getInputStream());
            medias.add(upload);
        }
        //4、将数据封装到Movement对象
        movement.setUserId(userId);
        movement.setMedias(medias);
        //5、调用movementApi完成发布动态
        movementApi.publish(movement);
    }

    /**
     查询我的动态
     * 请求参数	page,pagesize,userId
     * 响应结果	ResponseEntity<PageResult>
     * @param userId
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult findByUserId(Long userId, Integer page, Integer pagesize) {
        //1、根据用户id，调用API查询个人动态内容（PageResult  -- Movement）
        PageResult pr = movementApi.findByUserId(userId,page,pagesize);
        //2、获取PageResult中的item列表对象
        List<Movement> items = (List<Movement>) pr.getItems();
        //3、非空判断
        if(items == null) {
            return pr;
        }
        //4、循环数据列表
        UserInfo userInfo = userInfoApi.findById(userId);
        List<MovementsVo> vos = new ArrayList<>();
        for (Movement item : items) {
            //5、一个Movement构建一个Vo对象
            MovementsVo vo = MovementsVo.init(userInfo, item);
            vos.add(vo);
        }
        //6、构建返回值
        pr.setItems(vos);
        return pr;
    }

    /**
     * 查询当前用户的好友动态
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult findFriendMovements(Integer page, Integer pagesize) {
        //1.获取当前用户id
        Long userId = UserHolder.getUserId();
        //2.调用Api查询当前用户好友发布的动态列表(根据当前用户id,查询时间线表中发布动态的用户动态id,再根据动态id查询出动态详情)
        List<Movement> list=movementApi.findFriendMovements(page,pagesize,userId);
        return getPageResult(page, pagesize, list);
    }
    //抽取公共方法--refactor--EX  method
    private PageResult getPageResult(Integer page, Integer pagesize, List<Movement> list) {
        //3.判断查询出来的列表是否为空
        if(CollUtil.isEmpty(list)){
            return new PageResult();
        }
        //4.PageResult中的items是MovementVo对象,该对象需要userInfo和movement组合
        // 提取动态列表中的id列表，根据集合中对象的某个属性生成新的集合
        List<Long> userIds = CollUtil.getFieldValues(list, "userId", Long.class);
        //5.根据用户的id,查询用户的详细信息
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        //6.构造返回值，一个movement就封装成一个MovementVo对象
        List<MovementsVo> vos = new ArrayList<>();
        for (Movement movement : list) {
            UserInfo userInfo = map.get(movement.getUserId());
            if (userInfo !=null ){
                MovementsVo vo = MovementsVo.init(userInfo, movement);
                vos.add(vo);
            }
        }
        //封装进pageResult并返回
        return new PageResult(page, pagesize, Math.toIntExact(0l), vos);
    }

    /**
     * 查询推荐动态--根据redis中的pid进行查询
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult findRecommendMovements(Integer page, Integer pagesize) {
        //1.获取redis中的pid列表,首先拼接redis中的key
        String redisKey = Constants.MOVEMENTS_RECOMMEND+UserHolder.getUserId();
        String redisValue = redisTemplate.opsForValue().get(redisKey);
        //2.判断推荐pid数据是否存在，不存在则直接返回随机推荐，如果存在则截取数据，将数据转换数组后进一步操作
        List<Movement> list = Collections.EMPTY_LIST;//后续需要将List<Movement>--> List<MovementsVo>
        if (StringUtils.isEmpty(redisValue)){
            //3.如果不存在，则调用API随机构造10条动态数据
            list=movementApi.randomMovements(pagesize);
        }else {
            //4.如果存在，处理pid数据-->数组
            String[] values = redisValue.split(",");
            //5.判断越界问题，如果当前页的起始条数是否小于数组总数，只有小于才合法
            if ((page - 1)* pagesize < values.length){
                //通过stream流的方式解决分页，并转化为list集合，利用map可以对里面的数据进行加工处理
                List<Long> pids = Arrays.stream(values).skip((page - 1) * pagesize).limit(pagesize)
                        .map(e -> Long.valueOf(e)).collect(Collectors.toList());
                //6.调用API根据PID数组查询动态数据
                list = movementApi.findMovementsByPids(pids);
            }
        }
        //调用公共方法构建返回值List<MovementsVo>
        PageResult pageResult = getPageResult(page, pagesize, list);
        return getPageResult(page,pagesize,list);
    }

    /**
     * 查看单条动态
     * @param movementId
     * @return
     */
    public MovementsVo findById(String movementId) {
        //1、调用movementApi根据movementId查询动态详情
        Movement movement = movementApi.findById(movementId);
        //2、转化vo对象
        if(movement != null) {
            UserInfo userInfo = userInfoApi.findById(movement.getUserId());
            return MovementsVo.init(userInfo,movement);
        }else {
            return null;
        }

    }
}
