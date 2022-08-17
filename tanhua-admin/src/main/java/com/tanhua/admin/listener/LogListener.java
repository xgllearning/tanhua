package com.tanhua.admin.listener;

import com.alibaba.fastjson.JSON;
import com.tanhua.admin.mapper.LogMapper;
import com.tanhua.model.domain.Log;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LogListener {

    @Autowired
    private LogMapper logMapper;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "tanhua.log.queue",durable = "true"),
            exchange = @Exchange(name = "tanhua.log.exchange",type = ExchangeTypes.TOPIC),
            key = {"log.*"}
    ))
    public void listenCreate(String message){//监听器消费mq消息,发送的时候是什么类型接收就用什么类型
        //将message转为map
        Map map = JSON.parseObject(message, Map.class);
        map.forEach((k,v)-> System.out.println(k+"---"+v));
        Long userId = Long.valueOf((String) map.get("userId"));
        String type = (String) map.get("type");
        String logTime = (String) map.get("logTime");
        //执行保存操作
        Log log = new Log(userId,logTime,type);
        logMapper.insert(log);
    }
}
