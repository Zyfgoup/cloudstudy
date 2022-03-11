package com.zyfgoup.exception;


import org.springframework.http.HttpStatus;

/**
 * @Author Zyfgoup
 * @Date 2020/12/1 10:47
 * @Description
 * 异常处理的核心类 自定义的异常码
 * 使用枚举
 */

public enum ErrorCode {


    //枚举的类型之间用逗号隔开
    //定义很多很多的异常码
    RESOURCE_NOT_FOUND(1001,HttpStatus.BAD_REQUEST,"未找到该资源"),
    UPLOAD_AVATAR_FAILED(1003,HttpStatus.BAD_REQUEST,"上传头像失败"),
    DEL_CATEGORY_FAILED(1004,HttpStatus.INTERNAL_SERVER_ERROR,"删除分类失败"),
    READ_WORDS_FAILED(1005,HttpStatus.INTERNAL_SERVER_ERROR,"初始化敏感词库失败"),
    EMPTY_CATEGORY_FILE(1006,HttpStatus.BAD_REQUEST,"文档内容为空"),
    DELETE_COURSE_FAILED(1007,HttpStatus.INTERNAL_SERVER_ERROR,"删除课程失败"),
    REGISTER_FAILED(1008,HttpStatus.BAD_REQUEST,"注册失败"),
    TOKEN_EMPTY(1009,HttpStatus.BAD_REQUEST,"请登录"),
    TOKEN_ERROR(1010,HttpStatus.BAD_REQUEST,"请登录"),
    WX_ERROR(20001,HttpStatus.BAD_REQUEST,"登录失败"),
    RELOGIN(28004,HttpStatus.BAD_REQUEST,"请重新登录");

    private final int code;
    private final HttpStatus status;
    private final String message;


    ErrorCode(int code, HttpStatus status, String message){
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ErrorCode{" +
                "code=" + code +
                ", status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
