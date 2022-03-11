package com.zyfgoup.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @Author Zyfgoup
 * @Date 2021/3/6 17:11
 * @Description
 */
public class UserServiceException extends AuthenticationException {
    public UserServiceException() {
        super("用户服务未启动");
    }
}
