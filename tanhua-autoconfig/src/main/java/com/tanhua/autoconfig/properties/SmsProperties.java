package com.tanhua.autoconfig.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tanhua.sms")
public class SmsProperties {
//若想获取到yml的配置属性，需要指定对应的属性前缀，会自动复制到同名属性上
    private String signName;
    private String templateCode;
    private String accessKey;
    private String secret;

}
