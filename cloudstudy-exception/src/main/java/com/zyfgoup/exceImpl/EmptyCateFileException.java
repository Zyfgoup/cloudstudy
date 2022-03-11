package com.zyfgoup.exceImpl;

import com.zyfgoup.exception.BaseException;
import com.zyfgoup.exception.ErrorCode;

/**
 * @Author Zyfgoup
 * @Date 2021/3/1 19:47
 * @Description
 */
public class EmptyCateFileException  extends BaseException {

    public EmptyCateFileException() {
        super(ErrorCode.EMPTY_CATEGORY_FILE);
    }
}
