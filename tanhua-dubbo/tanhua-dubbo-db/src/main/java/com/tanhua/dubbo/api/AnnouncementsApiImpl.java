package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.dubbo.mappers.AnnouncementsMapper;
import com.tanhua.model.domain.Announcement;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@DubboService
public class AnnouncementsApiImpl implements AnnouncementsApi{
    @Autowired
    private AnnouncementsMapper announcementsMapper;

    @Override
    public List<Announcement> find(Integer page, Integer pagesize) {
        LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<>();
        Page<Announcement> iPage = new Page<>(page, pagesize);
        Page<Announcement> pages = announcementsMapper.selectPage(iPage, queryWrapper);
        List<Announcement> list = pages.getRecords();
//        for (Announcement announcement : list) {
//            Date date = announcement.getCreated();
//            announcement.setCreated();
////            String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
//        }
        return list;
    }
}
