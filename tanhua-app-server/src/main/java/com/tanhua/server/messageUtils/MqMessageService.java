package com.tanhua.server.messageUtils;

import com.alibaba.fastjson.JSON;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class MqMessageService {

    @Autowired
    private AmqpTemplate amqpTemplate;

    //发送日志消息，参数一：用户id,参数二：操作消息类型(数字)，参数三：用户相关user,动态相关movement,小视频相关video，参数四：推荐系统需要id,busId为业务id  动态id或者视频id
    public void sendLogMessage(Long userId,String type,String key,String busId) {
        try {
            Map map = new HashMap();
            map.put("userId",userId.toString());
            map.put("type",type);
            map.put("logTime",new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            map.put("busId",busId);
            String message = JSON.toJSONString(map);
            amqpTemplate.convertAndSend("tanhua.log.exchange", "log."+key,message);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }

    //发送动态审核消息
    public void sendAudiMessage(String movementId) {
        try {
            amqpTemplate.convertAndSend("tanhua.audit.exchange",  "audit.movement",movementId);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }
}