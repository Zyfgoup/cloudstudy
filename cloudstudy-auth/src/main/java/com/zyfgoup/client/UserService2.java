package com.zyfgoup.client;

import com.alibaba.fastjson.JSONObject;
import com.zyfgoup.client.fallback.UserClientFallBack;
import com.zyfgoup.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author Zyfgoup
 * @Date 2021/1/22 11:00
 * @Description
 */
@Service
@FeignClient(value = "cloudstudy-user",fallback = UserClientFallBack.class)
public interface UserService2 {

    /**
     * 调用user服务
     * @param name
     * @return
     */
    @GetMapping("/out/get/{name}")
    Map<String,Object> getByName(@PathVariable("name") String name);


    @PostMapping("/user/add")
    Result add(@RequestBody Map<String,Object> map);


    @PostMapping("/user/login")
    Result login(@RequestBody Map<String,Object> map);

    @PostMapping("/permission/out/url")
    Result getPermissionUrl(@RequestBody String name);
}
