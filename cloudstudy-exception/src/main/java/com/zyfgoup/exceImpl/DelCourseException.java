package com.zyfgoup.exceImpl;

import com.zyfgoup.exception.BaseException;
import com.zyfgoup.exception.ErrorCode;

/**
 * @Author Zyfgoup
 * @Date 2021/3/3 1:10
 * @Description
 */
public class DelCourseException extends BaseException {
    public DelCourseException() {
        super(ErrorCode.DELETE_COURSE_FAILED);
    }
}
