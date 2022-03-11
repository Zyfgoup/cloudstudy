package com.zyfgoup.client.fallback;

import com.zyfgoup.client.OrderClient;
import com.zyfgoup.entity.Result;
import org.hibernate.validator.internal.util.logging.Log_$logger;

/**
 * @Author Zyfgoup
 * @Date 2021/3/23 3:15
 * @Description
 */
public class OrderClientFallBack implements OrderClient {
    @Override
    public Result deleteCartByCourseId(String id) {
        return Result.fail("删除失败");
    }
}
