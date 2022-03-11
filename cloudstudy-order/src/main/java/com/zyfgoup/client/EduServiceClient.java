package com.zyfgoup.client;

import com.zyfgoup.client.fallback.EduServiceClientFallBack;
import com.zyfgoup.entity.CourseWebVoOrder;
import com.zyfgoup.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @Author Zyfgoup
 * @Date 2021/3/16 17:02
 * @Description
 */
@Service
@FeignClient(value = "cloudstudy-service",fallback = EduServiceClientFallBack.class)//调用的服务名称
public interface EduServiceClient {
    //根据课程id查询课程信息
    @PostMapping("/coursefront/getCourseInfoOrder/{id}")
    public CourseWebVoOrder getCourseInfoOrder(@PathVariable("id") String id);


    //根据课程id更改销售量
    @GetMapping("/course/updateBuyCount/{id}")
    public Result updateBuyCountById(@PathVariable("id") String id);
}
