package com.zyfgoup.entity;

import com.zyfgoup.utils.RedisKey;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @Author Zyfgoup
 * @Date 2020/12/10 19:35
 * @Description 停止服务的时候 删除Redis
 */
@Component
public class MyDisposableBean implements DisposableBean {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public void destroy() throws Exception {
        redisTemplate.delete(RedisKey.ALL_CATEGORY);
        redisTemplate.delete(RedisKey.ALL_COURSE);
        redisTemplate.delete(RedisKey.ALL_TEACHER);
    }
}
