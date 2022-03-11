package com.zyfgoup.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @Author Zyfgoup
 * @Date 2021/3/6 0:43
 * @Description
 */
public class DisabledException extends AuthenticationException {
    public DisabledException() {
        super("该账号已被停用");
    }
}
