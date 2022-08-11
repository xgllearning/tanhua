package com.tanhua.server.controller;

import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.service.TanhuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tanhua")
public class TanhuaController {

    @Autowired
    private TanhuaService tanhuaService;

    //今日佳人
    @GetMapping("/todayBest")
    public ResponseEntity todayBest() {
        TodayBest vo = tanhuaService.todayBest();
        return ResponseEntity.ok(vo);
    }

    /**推荐好友列表
     * 接口路径	/tanhua/recommendation
     * 请求方式	GET
     * 请求头	Authorization
     * 请求参数	RecommendUserDto
     * 响应结果	ResponseEntity<PageResult>
     */
    @GetMapping("/recommendation")
    public ResponseEntity recommendation(RecommendUserDto recommendUserDto) {
        //采用RecommendUserDto接收参数,需要保证属性名和参数名一致
        //带有分页的返回值vo对象是PageResult，只要是分页，统一返回PageResult,调用tanhuaService方法，传入接收的参数
        PageResult pageResult=tanhuaService.recommendation(recommendUserDto);
        return ResponseEntity.ok(pageResult);
    }


    /**
     * 联系人管理-查询佳人详情信息
     * 接口路径	/tanhua/:id/personalInfo
     * 请求方式	GET
     * 路径参数	:id
     * 响应结果	ResponseEntity<TodayBest>
     */
    @GetMapping("/{id}/personalInfo")
    public ResponseEntity personalInfo(@PathVariable("id") Long userId) {
        TodayBest best = tanhuaService.personalInfo(userId);
        return ResponseEntity.ok(best);
    }
}
