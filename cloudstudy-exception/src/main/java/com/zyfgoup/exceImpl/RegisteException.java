package com.zyfgoup.exceImpl;

import com.zyfgoup.exception.BaseException;
import com.zyfgoup.exception.ErrorCode;

/**
 * @Author Zyfgoup
 * @Date 2021/3/15 14:00
 * @Description
 */
public class RegisteException extends BaseException {
    public RegisteException() {
        super(ErrorCode.REGISTER_FAILED);
    }
}
