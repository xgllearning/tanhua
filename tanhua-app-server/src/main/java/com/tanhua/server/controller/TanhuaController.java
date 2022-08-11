package com.tanhua.server.controller;

import com.tanhua.model.dto.RecommendUserDto;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.service.TanhuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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


    /**查看陌生人问题
     * 接口路径	/tanhua/strangerQuestions
     * 请求方式	GET
     * 请求参数	userId
     * 响应结果	ResponseEntity<String>
     */
    @GetMapping("/strangerQuestions")
    public ResponseEntity strangerQuestions(Long userId) {
        String question=tanhuaService.strangerQuestions(userId);
        return ResponseEntity.ok(question);
    }

    /**好友申请（回复陌生人问题）
     * 想办法给环信服务端发送的
     * {
     *      "userId":1,
     *  	"huanXinId":“hx1",
     *     "nickname":"黑马小妹",
     *     "strangerQuestion":"你喜欢去看蔚蓝的大海还是去爬巍峨的高山？",
     *     "reply":"我喜欢秋天的落叶，夏天的泉水，冬天的雪地，只要有你一切皆可~"
     *  }
     *  思路：可以放到一个对象或者map中，然后通过json转换的工具类转换为json发送即可
     *  userId：当前操作用户的id，huanXinId：操作人的环信用户
     * niciname：当前操作人昵称
     * 接口路径	/tanhua/strangerQuestions
     * 请求方式	POST
     * 参数	Map
     * 响应结果	ResponseEntity<void>
     */
    @PostMapping("/strangerQuestions")
    public ResponseEntity replyQuestions(@RequestBody Map map) {
        //1.前端传递的userId:是Integer类型的,注意Interger转的话需要先toString再转Long
        // userId是对方陌生人id,reply是当前用户发送的信息
        String userId = map.get("userId").toString();
        Long toUserId = Long.valueOf(userId);
        String reply = map.get("reply").toString();
        //2.通过service处理逻辑，封装json对象发送个环信服务端
        tanhuaService.replyQuestions(toUserId,reply);
        return ResponseEntity.ok(null);
    }

}
