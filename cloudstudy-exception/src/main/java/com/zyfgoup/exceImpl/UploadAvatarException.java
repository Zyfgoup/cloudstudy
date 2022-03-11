package com.zyfgoup.exceImpl;

import com.zyfgoup.exception.BaseException;
import com.zyfgoup.exception.ErrorCode;

/**
 * @Author Zyfgoup
 * @Date 2021/1/21 17:13
 * @Description 上传失败异常
 */
public class UploadAvatarException extends BaseException {
    public UploadAvatarException() {
        super(ErrorCode.UPLOAD_AVATAR_FAILED);
    }
}
