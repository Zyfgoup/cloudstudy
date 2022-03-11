package com.zyfgoup.service.impl;

import cn.hutool.core.date.DateTime;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.zyfgoup.exceImpl.UploadAvatarException;
import com.zyfgoup.service.FileService;
import com.zyfgoup.utils.ConstantPropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.UUID;

/**
oss 上传组件
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {
    @Override
    public String upload(MultipartFile file) throws UploadAvatarException {
        OSSClient ossClient = null;
        String url = null;
        try {
            // 创建OSSClient实例。
            ossClient = new OSSClient(
                    ConstantPropertiesUtil.END_POINT,
                    ConstantPropertiesUtil.ACCESS_KEY_ID,
                    ConstantPropertiesUtil.ACCESS_KEY_SECRET);

            //获取文件名称
            String filename = file.getOriginalFilename();
            String type = filename.substring(filename.lastIndexOf("."));
            String newName = UUID.randomUUID().toString() + type;
            String dataPath = new DateTime().toString("yyyy/MM/dd");
            String urlPath = dataPath + "/" + newName;
            // 上传文件流。
            InputStream inputStream = file.getInputStream();
            ossClient.putObject(ConstantPropertiesUtil.BUCKET_NAME, urlPath, inputStream);
            url = "https://"+ConstantPropertiesUtil.BUCKET_NAME + "." + ConstantPropertiesUtil.END_POINT + "/" + urlPath;
        } catch (Exception e) {
            log.error("上传服务 {}","上传失败");
            throw new UploadAvatarException();
        } finally {
            // 关闭OSSClient。
            ossClient.shutdown();
        }
        return url;
    }

    @Override
    public void removeFile(String url) {
        String endPoint =  ConstantPropertiesUtil.END_POINT;
        String accessKeyId =  ConstantPropertiesUtil.ACCESS_KEY_ID;
        String accessKeySecret =  ConstantPropertiesUtil.ACCESS_KEY_SECRET;
        String bucketName =  ConstantPropertiesUtil.BUCKET_NAME;

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);

        String host = "https://" + bucketName + "." + endPoint + "/";
        String objectName = url.substring(host.length());
        ossClient.deleteObject(bucketName, objectName);

        // 关闭OSSClient。
        ossClient.shutdown();
    }
}
