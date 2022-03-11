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

    @Override
    public Result outGetOne(String userid) {
        return Result.fail("调用user服务失败");
    }
}
