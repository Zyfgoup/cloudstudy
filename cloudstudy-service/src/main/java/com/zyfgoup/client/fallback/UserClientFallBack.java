package com.zyfgoup.client.fallback;
import com.zyfgoup.client.UserClient;
import com.zyfgoup.entity.Result;
import lombok.extern.slf4j.Slf4j;


/**
 * @Author Zyfgoup
 * @Date 2021/3/10 23:38
 * @Description
 */
@Slf4j
public class UserClientFallBack  implements UserClient {
    //出错之后会执行
    @Override
    public Result countRegister(String day) {
        log.error("调用user服务失败");
        return Result.fail("生成失败");
    }

    @Override
    public Result removeAvaTar(String url) {
        log.error("调用user服务失败");
        return Result.fail("删除失败");
    }

    @Override
    public Result outGetOne(String userid) {
        return null;
    }
}
