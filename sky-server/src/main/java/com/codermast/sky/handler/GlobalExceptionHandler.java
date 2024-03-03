package com.codermast.sky.handler;

import com.codermast.sky.constant.MessageConstant;
import com.codermast.sky.exception.BaseException;
import com.codermast.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    // 处理用户名重复异常
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        // 这里是通过对报错信息的分析来判断的，一旦 Spring 框架修改了该报错信息的提示，则代码就失效了
        // 也可以使用其他的办法：例如再次查询数据库中是否有该用户，这样会造成服务器性能的浪费。
        String message = ex.getMessage();

        if (message.contains("Duplicate entry")){
            String[] split = message.split(" ");

            String username = split[2];

            String msg = username + MessageConstant.ALREADY_EXISTS;

            return Result.error(msg);
        }else {
            return Result.error("未知错误");
        }
    }
}
