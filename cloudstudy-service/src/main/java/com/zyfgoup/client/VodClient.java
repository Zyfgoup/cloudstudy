package com.zyfgoup.client;

import com.zyfgoup.client.fallback.VodClientFallBack;
import com.zyfgoup.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author Zyfgoup
 * @Date 2021/3/2 20:40
 * @Description
 */
@Service
@FeignClient(value = "cloudstudy-vod",fallback = VodClientFallBack.class)
public interface VodClient {
    @DeleteMapping(value = "/video/{videoSourceId}")
    Result removeVideo(@PathVariable("videoSourceId") String videoSourceId);

    @DeleteMapping(value = "/video/removeList")
    Result removeVideoList(@RequestParam("videoIdList") List<String> videoIdList);
}
