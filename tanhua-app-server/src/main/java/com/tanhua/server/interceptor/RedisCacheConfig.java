package com.tanhua.server.interceptor;



import com.google.common.collect.ImmutableMap;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.time.Duration;
import java.util.Map;

@Configuration
public class RedisCacheConfig {

    //设置失效时间
    private static final Map<String, Duration> cacheMap;

    static {
        //配置哪些缓存需要设置过期时间 以及过期时间是多少
        cacheMap = ImmutableMap.<String, Duration>builder().put("videos", Duration.ofSeconds(30L)).build();
//        cacheMap.put("")  //添加新的缓存失效时间
    }

    //配置RedisCacheManagerBuilderCustomizer对象
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> {
            //根据不同的cachename设置不同的失效时间
            for (Map.Entry<String, Duration> entry : cacheMap.entrySet()) {
                builder.withCacheConfiguration(entry.getKey(),
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(entry.getValue()));
            }
        };
    }
}
