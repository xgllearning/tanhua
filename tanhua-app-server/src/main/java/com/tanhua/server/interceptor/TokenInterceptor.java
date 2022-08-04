package com.tanhua.server.interceptor;


import com.tanhua.commons.utils.JwtUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//拦截器，前置拦截进行处理
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.从request中获取请求头
        String token = request.getHeader("Authorization");
        //2.JwtUtils工具类解析token，判断token是否合法
        boolean verifyToken = JwtUtils.verifyToken(token);
        //3.不合法则拦截，响应401状态码，拦截
        if (!verifyToken){
            response.setStatus(401);
            return false;
        }
        //4.合法对请求放行

        return true;
    }
}
