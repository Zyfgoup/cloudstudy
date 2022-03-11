package com.zyfgoup.client.fallback;

import com.zyfgoup.client.VodClient;
import com.zyfgoup.entity.Result;

import java.util.List;

/**
 * @Author Zyfgoup
 * @Date 2021/3/3 14:57
 * @Description
 */
public class VodClientFallBack implements VodClient {
    @Override
    public Result removeVideo(String videoSourceId) {
        return Result.fail("删除失败");
    }

    @Override
    public Result removeVideoList(List<String> videoIdList) {
        return Result.fail("删除失败");
    }
}
