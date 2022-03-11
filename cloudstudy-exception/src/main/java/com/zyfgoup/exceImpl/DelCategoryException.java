package com.zyfgoup.exceImpl;


import com.zyfgoup.exception.BaseException;
import com.zyfgoup.exception.ErrorCode;

/**
 * @Author Zyfgoup
 * @Date 2020/12/1 11:12
 * @Description
 */
public class DelCategoryException extends BaseException {

    //构建一个异常类只需要传入一个简单的数据即可  异常码是写死的了
    public DelCategoryException(){
        super(ErrorCode.DEL_CATEGORY_FAILED);
    }
}
