package com.zyfgoup;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author Zyfgoup
 * @Date 2021/1/20 17:47
 * @Description
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(value = "com.zyfgoup.mapper")
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class,args);
    }
}
