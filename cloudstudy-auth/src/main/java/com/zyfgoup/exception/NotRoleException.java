package com.zyfgoup.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @Author Zyfgoup
 * @Date 2021/3/6 17:01
 * @Description
 */
public class NotRoleException extends AuthenticationException {
    public NotRoleException() {
       super("该账号未被分配角色，请联系管理员");
    }
}
