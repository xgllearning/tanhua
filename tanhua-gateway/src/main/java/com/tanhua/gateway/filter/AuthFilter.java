package com.tanhua.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanhua.commons.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
//拥有gateway此过滤器之后，app-server中的拦截器就不需要了，TokenInterceptor implements HandlerInterceptor
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    //把不需要校验的连接放到list集合中，通过@value注入
    @Value("${gateway.excludedUrls}")
    private List<String> excludeUrls;

    //过滤器核心业务代码
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        for (String excludeUrl : excludeUrls) {
            System.out.println(excludeUrl);//打印出排除的url，查看是否成功注入
        }

        //从filter完成权限校验
        //1.获取当前的请求链接
        String path = exchange.getRequest().getURI().getPath();
        System.out.println("url:"+path);
        //2.判断excludeUrls是否包含path，包含的话则放行，不需要校验
        if(excludeUrls.contains(path)){
            return chain.filter(exchange);//放行，向后执行
        }
        //3.获取token(有时候token前面携带某些参数，后台系统页面发送的token以"Bearer "开头，需要处理)
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if(!StringUtils.isEmpty(token)){
            token = token.replace("Bearer ", "");//替换所有匹配到Bearer 的字符串
        }
        //4.调用工具类解析token
        boolean verifyToken = JwtUtils.verifyToken(token);
        //5.如果校验失败则返回错误状态
        ServerHttpResponse response = exchange.getResponse();
        if(!verifyToken){
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("errCode", 401);
            responseData.put("errMessage", "用户未登录");
            return responseError(response,responseData);//调用方法，返回响应错误数据
        }
        //6.之后是成功
        return chain.filter(exchange);//放行
    }

    //响应错误数据的方法，目的是将map集合转换成json返回
    private Mono<Void> responseError(ServerHttpResponse response, Map<String, Object> responseData){
        // 将信息转换为 JSON
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] data = new byte[0];
        try {
            data = objectMapper.writeValueAsBytes(responseData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // 输出错误信息到页面
        DataBuffer buffer = response.bufferFactory().wrap(data);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    //配置过滤器的执行顺序
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;//低级别过滤
    }
}
