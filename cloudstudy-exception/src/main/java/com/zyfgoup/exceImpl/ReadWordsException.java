package com.zyfgoup.exceImpl;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.zyfgoup.exception.BaseException;
import com.zyfgoup.exception.ErrorCode;

/**
 * @Author Zyfgoup
 * @Date 2021/1/29 10:31
 * @Description
 */
public class ReadWordsException extends BaseException {
    public ReadWordsException() {
        super(ErrorCode.READ_WORDS_FAILED);
    }
}
