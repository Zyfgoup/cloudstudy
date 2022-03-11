package com.zyfgoup.controller.front;


import com.alibaba.fastjson.JSON;
import com.zyfgoup.entity.CmsAd;
import com.zyfgoup.entity.Result;
import com.zyfgoup.service.CmsAdService;
import com.zyfgoup.utils.RedisKey;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/front-cms")
@Slf4j
public class ApiAdController {

    @Autowired
    private CmsAdService adService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @ApiOperation("根据推荐位id显示广告推荐")
    @GetMapping("list/{adTypeId}")
    public Result listByAdTypeId(@ApiParam(value = "推荐位id", required = true) @PathVariable String adTypeId) {

        if(redisTemplate.hasKey(RedisKey.BANNER)){
            List<CmsAd> cmsAds = JSON.parseArray(redisTemplate.opsForValue().get(RedisKey.BANNER), CmsAd.class);
            return Result.succ(cmsAds);
        }else {
            List<CmsAd> adList = adService.selectByAdTypeId(adTypeId);
            redisTemplate.opsForValue().set(RedisKey.BANNER, JSON.toJSONString(adList),1, TimeUnit.DAYS);
            return Result.succ(adList);
        }
    }


}
