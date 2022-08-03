package com.tanhua.autoconfig;


import com.tanhua.autoconfig.properties.AipFaceProperties;
import com.tanhua.autoconfig.properties.OssProperties;
import com.tanhua.autoconfig.properties.SmsProperties;
import com.tanhua.autoconfig.template.AipFaceTemplate;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.autoconfig.template.SmsTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
//会自动读取yml中的配置属性，接着把SmsProperties、OssTemplate、AipFaceTemplate对象存到容器
@EnableConfigurationProperties({
        SmsProperties.class,OssProperties.class, AipFaceProperties.class
})
public class TanhuaAutoConfiguration {
    //定义自动装配类TanhuaAutoConfiguration，将smsTemplate交给ioc容器管理
    @Bean
    public SmsTemplate smsTemplate(SmsProperties properties) {
//        从容器中获取properties，调用SmsTemplate并存放容器
        return new SmsTemplate(properties);
    }

    @Bean
    public OssTemplate ossTemplate(OssProperties ossProperties){
        return new OssTemplate(ossProperties);
    }
    //将AipFaceTemplate模板加入ioc容器
    @Bean
    public AipFaceTemplate aipFaceTemplate(){
        return new AipFaceTemplate();
    }
}
