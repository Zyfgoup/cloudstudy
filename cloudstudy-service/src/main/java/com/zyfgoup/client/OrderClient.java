package com.zyfgoup.client;

import com.zyfgoup.client.fallback.OrderClientFallBack;
import com.zyfgoup.client.fallback.UserClientFallBack;
import com.zyfgoup.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author Zyfgoup
 * @Date 2021/3/23 3:14
 * @Description
 */
@Service
@FeignClient(value = "cloudstudy-order",fallback = OrderClientFallBack.class)
public interface OrderClient {


    @DeleteMapping("/cart/remove/courseId/{id}")
    public Result deleteCartByCourseId(@PathVariable("id") String id);


}
