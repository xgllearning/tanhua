package cn.itcast.mq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FanoutConfig {
    //在consumer服务中，利用代码声明队列、交换机，并将两者绑定
    //1.声明交换机itcast.fanout
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange("itcast.fanout");
    }

    //2.声明队列fanout.queue1
    @Bean
    public Queue queue1(){
        return new Queue("fanout.queue1");
    }
    //2.声明队列fanout.queue2
    @Bean
    public Queue queue2(){
        return new Queue("fanout.queue2");
    }
    //3.声明绑定关系,绑定队列1到交换机
    @Bean
    public Binding fanoutBinding1(Queue queue1,FanoutExchange fanoutExchange){
        return BindingBuilder.bind(queue1).to(fanoutExchange);
    }
    //3.声明绑定关系,绑定队列2到交换机
    @Bean
    public Binding fanoutBinding2(Queue queue2,FanoutExchange fanoutExchange){
        return BindingBuilder.bind(queue2).to(fanoutExchange);
    }

    @Bean
    public Queue objectQueue(){
        return new Queue("object.queue");
    }
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
