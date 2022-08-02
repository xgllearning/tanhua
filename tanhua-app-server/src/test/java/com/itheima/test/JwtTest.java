package com.itheima.test;

import io.jsonwebtoken.*;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    @Test
    public void testCreateToken() {
        //使用jwt工具类生成token
        //1、准备数据
        Map map = new HashMap();
        map.put("id",1);
        map.put("mobile","13800138000");
        //2、使用JWT的工具类生成token
        //获取当前时间
        long now = System.currentTimeMillis();
        String token = Jwts.builder() //jwt工具类，链式编程
                .signWith(SignatureAlgorithm.HS512, "study") //指定加密算法+密钥
                .setClaims(map) //写入数据
                .setExpiration(new Date(now + 50000)) //指定失效时间
                .compact();
        System.out.println(token);
    }

    //解析token

    /**
     * SignatureException : token不合法
     * ExpiredJwtException：token已过期
     */
    @Test
    public void testParseToken() {
        String token = "" + "eyJhbGciOiJIUzUxMiJ9" +
                ".eyJtb2JpbGUiOiIxMzgwMDEzODAwMCIsImlkIjoxLCJleHAiOjE2NTk0NDUxODh9" +
                ".9vkq1P9gouZeOAM1_UZ1PXEvYqRlxdmmY-UFva--WaZJWq8HamTH_DBgFPkV2RidEqxdVbc9Jas5FQk7PgXvgA";
        try {
            //解析token工具类，Claims对象中就可以获取所有存入的对象
            Claims claims = Jwts.parser()
                    .setSigningKey("study")//写入解密的密钥
                    .parseClaimsJws(token)
                    .getBody();
            Object id = claims.get("id");
            Object mobile = claims.get("mobile");
            System.out.println(id + "--" + mobile);
        }catch (ExpiredJwtException e) {
            System.out.println("token已过期");
        }catch (SignatureException e) {
            System.out.println("token不合法");
        }

    }
}
