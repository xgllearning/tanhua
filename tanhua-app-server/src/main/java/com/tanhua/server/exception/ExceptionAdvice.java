package com.tanhua.server.exception;

import com.tanhua.model.vo.ErrorResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 定义统一异常处理类
 *  1、通过注解，声明异常处理类@ControllerAdvice
 *  2、编写方法，在方法内部处理异常，构造响应数据(与controller写法类似,也可以指定返回值类型，在参数上可以获取当前异常类型)
 *  3、方法上编写注解，指定此方法可以处理的异常类型
 *  4.定义完成之后，就不需要在controller层进行try-catch了
 */
@ControllerAdvice
public class ExceptionAdvice {
    //只处理处理业务异常，配置注解@ExceptionHandler，指定处理异常类型
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity handlerException(BusinessException be) {
        //打印出堆栈信息，显示报错信息
        be.printStackTrace();
        //为service层或其他层抛出的业务异常
        ErrorResult errorResult = be.getErrorResult();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
    }

    //只处理不可预知的异常，配置注解@ExceptionHandler，指定处理异常类型
    @ExceptionHandler(Exception.class)
    public ResponseEntity handlerException1(Exception be) {
        //打印出堆栈信息，显示报错信息
        be.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResult.error());
    }
}
