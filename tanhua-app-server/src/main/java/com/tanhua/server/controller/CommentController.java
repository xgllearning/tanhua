package com.tanhua.server.controller;

import com.tanhua.model.vo.PageResult;
import com.tanhua.server.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;




    /**
     * 分页查询评论列表
     * @param page
     * @param pagesize
     * @param movementId
     * @return
     */
    @GetMapping
    public ResponseEntity findComments(@RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pagesize,
                                       String movementId) {
        PageResult pr = commentService.findComments(movementId,page,pagesize);
        return ResponseEntity.ok(pr);
    }

    /**发布评论
     * 接口路径	/comments
     * 请求方式	POST
     * 请求参数	Map
     * 响应结果	ResponseEntity<void>
     * @param map
     * @return
     */
    @PostMapping
    public ResponseEntity saveComments(@RequestBody Map map){
        //获取mao携带的参数movementId、comment评论内容
        String comment = (String) map.get("comment");
        String movementId = (String) map.get("movementId");
        //调用service层封装对象执行逻辑
        commentService.saveComments(movementId,comment);
        return ResponseEntity.ok(null);
    }

    /**
     * 评论点赞
     * 接口路径	/comments/:id/like
     * 请求方式	GET
     * 路径参数	:id
     * 响应结果	ResponseEntity<Integer>
     * @param commentId
     * @return
     */
    @GetMapping("/{id}/like")
    public ResponseEntity like(@PathVariable("id") String commentId) {
        Integer likeCount = commentService.likeMovementComment(commentId);
        return ResponseEntity.ok(likeCount);
    }
    /**
     * 取消评论点赞
     * 接口路径	/movements/:id/dislike
     * 请求方式	GET
     * 路径参数	:id
     * 响应结果	ResponseEntity<Integer>
     * @param commentId
     * @return
     */
    @GetMapping("/{id}/dislike")
    public ResponseEntity dislike(@PathVariable("id") String commentId) {
        Integer likeCount = commentService.dislikeMovementComment(commentId);
        return ResponseEntity.ok(likeCount);
    }
}
