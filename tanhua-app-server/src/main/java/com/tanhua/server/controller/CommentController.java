package com.tanhua.server.controller;

import com.tanhua.server.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**发布评论
     * 接口路径	/comments
     * 请求方式	POST
     * 请求参数	Map
     * 响应结果	ResponseEntity<void>
     * @param map
     * @return
     */
    public ResponseEntity saveComments(@RequestBody Map map){
        //获取mao携带的参数movementId、comment评论内容
        String comment = (String) map.get("comment");
        String movementId = (String) map.get("movementId");
        //调用service层封装对象执行逻辑
        commentService.saveComments(movementId,comment);
        return ResponseEntity.ok(null);
    }

}
