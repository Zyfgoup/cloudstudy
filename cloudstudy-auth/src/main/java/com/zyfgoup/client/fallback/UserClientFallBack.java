package com.zyfgoup.client.fallback;

import com.zyfgoup.client.UserService2;
import com.zyfgoup.entity.Result;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @Author Zyfgoup
 * @Date 2021/3/2 0:13
 * @Description
 */
@Slf4j
public class UserClientFallBack implements UserService2 {
    @Override
    public Map<String, Object> getByName(String name) {
       return null;
    }

    @Override
    public Result add(Map<String, Object> map) {
        return Result.fail("出错了");
    }

    @Override
    public Result login(Map<String, Object> map) {
        return Result.fail("出错了");
    }

    @Override
    public Result getPermissionUrl(String name) {
        return Result.fail("出错了");
    }
}
