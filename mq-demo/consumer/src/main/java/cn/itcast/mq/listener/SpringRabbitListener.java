package cn.itcast.mq.listener;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Map;

@Component
public class SpringRabbitListener {

     @RabbitListener(queues = "simple.queue")//发的消息是什么类型，接收就要使用什么类型
     public void listenSimpleQueue(String msg) {
         System.out.println("消费者接收到simple.queue的消息：【" + msg + "】");
     }


}
