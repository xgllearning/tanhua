package com.tanhua.server.exception;

import com.tanhua.model.vo.ErrorResult;
import lombok.Data;

/**
 * 自定义异常类
 */
@Data
public class BusinessException extends RuntimeException {

    private ErrorResult errorResult;

    public BusinessException(ErrorResult errorResult) {
        //调用父类的方法，将errorResult消息传递父类
        super(errorResult.getErrMessage());
        this.errorResult = errorResult;
    }
}