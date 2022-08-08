package com.tanhua.server.controller;

import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.service.MovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/movements")
public class MovementController {

    @Autowired
    private MovementService movementService;

    /**
     * 发布动态，imageContet-可以携带多张文件，采用数组
     * textContent文字动态
     * location位置longitude经度
     * latitude纬度
     * 其余通过对象接收Movement
     * @param movement
     * @param imageContent
     * @return
     */
    @PostMapping
    public ResponseEntity movements(Movement movement, MultipartFile[] imageContent) throws IOException {
        //调用movementService处理逻辑
        movementService.publishMovement(movement,imageContent);
        return ResponseEntity.ok(null);
    }


    /**
     * 查询我的动态
     * 接口路径	/movements/all
     * 请求方式	GET
     * 请求参数	page,pagesize,userId
     * 响应结果	ResponseEntity<PageResult>
     */
    @GetMapping("/all")
    public ResponseEntity findByUserId(Long userId,
                                       @RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult pr = movementService.findByUserId(userId,page,pagesize);
        return ResponseEntity.ok(pr);
    }
}
