package com.zyfgoup.client;

import com.zyfgoup.entity.Result;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author Zyfgoup
 * @Date 2021/3/12 11:23
 * @Description
 */
@Slf4j
public class FileClientFallBack implements FileClient{
    @Override
    public Result removeFile(String url) {
        log.error("调用user服务失败");
        return Result.fail("删除失败");
    }
}
