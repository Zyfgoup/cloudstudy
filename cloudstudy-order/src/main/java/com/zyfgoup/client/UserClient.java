package com.zyfgoup.client;

import com.zyfgoup.client.fallback.UserClientFallBack;
import com.zyfgoup.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author Zyfgoup
 * @Date 2021/3/10 23:37
 * @Description
 */
@Service
@FeignClient(value = "cloudstudy-user",fallback = UserClientFallBack.class)
public interface UserClient {

    @GetMapping("/user/out/get/{userid}")
    Result outGetOne(@PathVariable("userid") String userid);
}
