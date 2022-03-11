package com.zyfgoup.client.fallback;

import com.zyfgoup.client.EduServiceClient;
import com.zyfgoup.entity.CourseWebVoOrder;
import com.zyfgoup.entity.Result;

/**
 * @Author Zyfgoup
 * @Date 2021/3/16 17:04
 * @Description
 */
public class EduServiceClientFallBack implements EduServiceClient {
    @Override
    public CourseWebVoOrder getCourseInfoOrder(String id) {
        return null;
    }

    @Override
    public Result updateBuyCountById(String id) {
        return Result.fail("调用service服务失败");
    }
}
