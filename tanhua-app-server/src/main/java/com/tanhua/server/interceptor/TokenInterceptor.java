package com.tanhua.server.interceptor;


import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.User;
import io.jsonwebtoken.Claims;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//当加入网关之后，在网关设置好过滤器之后，就不需要此拦截器再解析不合法的了，因为此时进来的都是合法的
//拦截器，前置拦截进行处理
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.从request中获取请求头
        String token = request.getHeader("Authorization");
//        //2.JwtUtils工具类解析token，判断token是否合法
//        boolean verifyToken = JwtUtils.verifyToken(token);
//        //3.不合法则拦截，响应401状态码，拦截
//        if (!verifyToken){
//            response.setStatus(401);
//            return false;
//        }
        //4.合法对请求放行
        //4.1解析token,获取id和手机号码，构造user对象，存入ThreadLocal
        Claims claims = JwtUtils.getClaims(token);
        String mobile = (String) claims.get("phone");
        Integer id = (Integer) claims.get("id");

        User user = new User();
        user.setId(Long.valueOf(id));
        user.setMobile(mobile);
        //存入ThreadLocal
        UserHolder.set(user);
        //由此可以省略解析的步骤
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.remove();
    }
}
