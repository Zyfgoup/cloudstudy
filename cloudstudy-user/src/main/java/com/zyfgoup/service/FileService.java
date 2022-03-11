package com.zyfgoup.service;

import com.zyfgoup.exceImpl.UploadAvatarException;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 */
public interface FileService {

    /**
     * 文件上传至阿里云
     * @param file
     * @return
     */
    String upload(MultipartFile file) throws UploadAvatarException;

    /**
     * 阿里云oss 文件删除
     * @param url 文件的url地址
     */
    void removeFile(String url);

}
