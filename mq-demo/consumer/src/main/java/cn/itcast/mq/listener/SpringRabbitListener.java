package cn.itcast.mq.listener;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.xml.ws.BindingType;
import java.time.LocalTime;
import java.util.Map;

@Component
public class SpringRabbitListener {

//     @RabbitListener(queues = "simple.queue")//发的消息是什么类型，接收就要使用什么类型
//     public void listenSimpleQueue(String msg) {
//         System.out.println("消费者接收到simple.queue的消息：【" + msg + "】");
//     }

    @RabbitListener(queues = "simple.queue")
    public void listenWorkQueue1(String msg) throws InterruptedException {
        System.out.println("消费者1接收到消息：【" + msg + "】" + LocalTime.now());
        Thread.sleep(20);
    }

    @RabbitListener(queues = "simple.queue")
    public void listenWorkQueue2(String msg) throws InterruptedException {
        System.err.println("消费者2........接收到消息：【" + msg + "】" + LocalTime.now());
        Thread.sleep(200);
    }


    @RabbitListener(queues = "fanout.queue1")
    public void listenFanoutQueue1(String msg) {
        System.out.println("消费者接收到fanout.queue1的消息：【" + msg + "】");
    }
    @RabbitListener(queues = "fanout.queue2")
    public void listenFanoutQueue2(String msg) {
        System.out.println("消费者接收到fanout.queue2的消息：【" + msg + "】");
    }

    //代码改进，通过@RabbitListener声明队列，交换机，绑定关系，而不用再通过@Bean的形式
    //Exchange将消息路由到BindingKey与消息RoutingKey一致的队列

    @RabbitListener(bindings = @QueueBinding(//队列绑定
            value = @Queue(name = "direct.queue1"),//value代表队列
            exchange = @Exchange(name = "itcast.direct", type = ExchangeTypes.DIRECT),//exchange是交换机
            key = {"red", "blue"}//key是bindingKey
    ))
    public void listenDirectQueue1(String msg){
        System.out.println("消费者接收到direct.queue1的消息：【" + msg + "】");
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue2"),
            exchange = @Exchange(name = "itcast.direct",type = ExchangeTypes.DIRECT),
            key = {"red","yellow"}
    ))
    public void listenDirectQueue2(String msg){
        System.out.println("消费者接收到direct.queue2的消息：【" + msg + "】");
    }

}
