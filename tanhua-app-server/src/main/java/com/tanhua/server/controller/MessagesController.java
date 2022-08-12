package com.tanhua.server.controller;

import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.service.MessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/messages")
public class MessagesController {

    @Autowired
    private MessagesService messagesService;

    /**
     * 根据环信ID查询用户详细信息
     * 接口路径	/messages/userinfo
     * 请求方式	GET
     * 请求参数	huanxinId
     * 响应结果	ResponseEntity<UserInfoVO>
     */
    @GetMapping("/userinfo")
    public ResponseEntity userinfo(String huanxinId) {
        UserInfoVo vo = messagesService.findUserInfoByHuanxin(huanxinId);
        return ResponseEntity.ok(vo);
    }

    /**添加好友
     * 接口路径	/messages/contacts
     * 请求方式	POST
     * 参数	Map
     * 响应结果	ResponseEntity<void>
     */
    @PostMapping("/contacts")
    public ResponseEntity contacts(@RequestBody Map map) {
        //提取参数userId，是Integer类型，转为long
        String userId = map.get("userId").toString();
        Long friendId = Long.valueOf(userId);
        //注意：传递过来的userId是发送申请的那个用户,即friendId
        messagesService.contacts(friendId);

        return ResponseEntity.ok(null);
    }

}