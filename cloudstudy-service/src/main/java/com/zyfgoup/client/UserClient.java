package com.zyfgoup.client;

import com.baomidou.mybatisplus.extension.api.R;
import com.zyfgoup.client.fallback.UserClientFallBack;
import com.zyfgoup.client.fallback.VodClientFallBack;
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

    /**
     * 查询某天注册人数
     * @param day
     * @return
     */
    @GetMapping("/user/countRegister/{day}")
    Result countRegister(@PathVariable("day") String day);


    @DeleteMapping("/file/remove")
    Result removeAvaTar(@RequestBody String url);

    @GetMapping("/user/out/get/{userid}")
    public Result outGetOne(@PathVariable("userid") String userid);
}
