package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.PageUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.commons.utils.Constants;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VideoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Video;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.VideoVo;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SmallVideosService {

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private FastFileStorageClient client;

    @Autowired
    private FdfsWebServer fdfsWebServer;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @DubboReference
    private VideoApi videoApi;

    @DubboReference
    private UserInfoApi userInfoApi;
    /**
     * 上传视频到服务器，通过fastDfs上传大文件，oss上传小文件图片
     * @param videoThumbnail
     * @param videoFile
     */
    public void saveVideos(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        //1.首先判断前台有没有传过来文件，如果没有传递则抛出异常
        if(videoThumbnail.isEmpty()||videoFile.isEmpty()){
            throw new BusinessException(ErrorResult.error());
        }
        //2.目的是封装video对象
        //2.1操作视频videoFile，获取其后缀
        String filename = videoFile.getOriginalFilename();
        filename = filename.substring(filename.lastIndexOf(".") + 1);
        StorePath storePath = client.uploadFile(videoFile.getInputStream(), videoFile.getSize(), filename, null);
        //2.2拼接视频完整路径
        String videoUrl = fdfsWebServer.getWebServerUrl()+storePath.getFullPath();
        Video video = new Video();
        video.setVideoUrl(videoUrl);
        //2.3操作videoThumbnail图片url
        String picUrl = ossTemplate.upload(videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());
        video.setPicUrl(picUrl);
        video.setUserId(UserHolder.getUserId());
        video.setText("我就是我，不一样的烟花");
        //3.调用api保存数据,通过保存成功返回id来判断是否保存成功，保存失败则抛出异常
        String videoId=videoApi.save(video);
        if (StringUtils.isEmpty(videoId)){
            throw new BusinessException(ErrorResult.error());
        }

    }

    /**
     * 分页查询视频列表
     * @param page
     * @param pagesize
     * @return
     */
    public PageResult queryVideoList(Integer page, Integer pagesize) {
        //1.先去redis中查询是否存在推荐数据，拼接redisKey
        String redisKey= Constants.VIDEOS_RECOMMEND+UserHolder.getUserId();
        String redisValue = redisTemplate.opsForValue().get(redisKey);
        //2.判断redis中是否为空，如果不为空则通过redis中的vid查询数据
        ArrayList<Video> videos = new ArrayList<>();
        int redisPages=0;//标记redis处理的页码
        if (!StringUtils.isEmpty(redisValue)){
            //3.不为空，切割redis中数据
            String[] vids = redisValue.split(",");
            //4.判断当前页的起始条数是否小于数组总数
            if ((page-1)*pagesize<vids.length){//将数组vids转为集合收集起来
                List<Long> vidList = Arrays.stream(vids).skip((page - 1) * pagesize).limit(pagesize)
                        .map(e -> Long.valueOf(e)).collect(Collectors.toList());
            //5.调用api,根据vid查询video数据
                videos=videoApi.findMovementsByVids(vidList);
            }
            redisPages = PageUtil.totalPage(vids.length, pagesize);//计算最大页码数
        }
        //6.如果redis中数据不存在，则直接分页查询video表视频数据
        //7.可能页码在第二页，但是redis数据只有一页，此时redis数据不为空，但是进行了越界处理，此时videos集合为空
        if (videos.isEmpty()){//处理越界问题+redis为空问题，查询video表，根据页码和条数查询
            //page计算规则，传入页码-redis查询的总页数
            videos=videoApi.queryVideoList((page-redisPages),pagesize);
        }
        //8.提取视频列表中所有的用户id
        List<Long> userIds = CollUtil.getFieldValues(videos, "userId", Long.class);
        //9.根据id查询用户的详细信息
        Map<Long, UserInfo> map = userInfoApi.findByIds(userIds, null);
        //10.构建返回值，返回值需要VideoVo init(UserInfo userInfo, Video item)
        ArrayList<VideoVo> vos = new ArrayList<>();
        for (Video video : videos) {
            UserInfo userInfo = map.get(video.getUserId());
            if (userInfo!=null){
                VideoVo vo = VideoVo.init(userInfo, video);
                vos.add(vo);
            }
        }

        return new PageResult(page,pagesize, Math.toIntExact(0l),vos);
    }
}
