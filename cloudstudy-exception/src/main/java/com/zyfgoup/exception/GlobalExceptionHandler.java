package com.zyfgoup.exception;

import com.zyfgoup.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author Zyfgoup
 * @Date 2020/12/1 11:15
 * @Description
 * 全局异常处理类
 * @RestControllerAdvive处理全部Controller 或者指定某些Controller
 * = ControllerAdvice + ResponseBody
 */

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 自定义异常的通用  具体的异常信息在ErrorCode和对应的自定义异常类里定义
     * @param ex
     *
     * @return
     */
    @ExceptionHandler(BaseException.class)
    public Result handleAppException(BaseException ex){
        log.error("异常信息 {}",ex.getErrorCode()+":"+ex.getErrorMsg());
        return Result.fail(ex.getErrorCode(),ex.getErrorMsg(),null);
    }


//    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(ResourceNotFoundException.class)
//    public Result handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request){
//        return Result.fail(ex.getErrorCode(),ex.getErrorMsg(),null);
//    }

    /**
     *  不太确定的错误 统一500异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAppException(Exception ex){
        log.error("不确定异常 {}",ex.getMessage());
        return new ResponseEntity<>(Result.fail(1000,"系统错误",null),new HttpHeaders(),HttpStatus.INTERNAL_SERVER_ERROR);
    }



}
