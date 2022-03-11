package com.zyfgoup;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author Zyfgoup
 * @Date 2021/3/12 10:48
 * @Description
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@MapperScan(value = "com.zyfgoup.mapper")
public class CmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CmsApplication.class);
    }
}

