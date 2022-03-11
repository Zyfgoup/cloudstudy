package com.zyfgoup.client;

import com.zyfgoup.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author Zyfgoup
 * @Date 2021/3/12 11:22
 * @Description
 */
@Service
@FeignClient(value = "cloudstudy-user",fallback = FileClientFallBack.class)
public interface FileClient {

    @DeleteMapping("/file/remove")
    Result removeFile(@RequestBody String url);
}
