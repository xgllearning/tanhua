package com.tanhua.admin.controller;

import com.tanhua.admin.service.ManagerService;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manage")
public class ManageController {

    @Autowired
    private ManagerService managerService;

    /**
     * 用户列表
     * 接口路径	/manage/users
     * 请求方式	GET
     * 请求参数	page,pagesize
     * 响应结果	ResponseEntity<PageResult>
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("/users")
    public ResponseEntity users(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult result = managerService.findAllUsers(page,pagesize);
        return ResponseEntity.ok(result);
    }


}
