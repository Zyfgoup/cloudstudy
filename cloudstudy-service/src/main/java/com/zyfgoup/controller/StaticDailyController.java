package com.zyfgoup.controller;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.api.R;
import com.zyfgoup.entity.Result;
import com.zyfgoup.service.StaticDailyService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 网站统计日数据 前端控制器
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-10
 */
@RestController
@RequestMapping("/static/daily")
public class StaticDailyController {

    @Autowired
    private StaticDailyService staticDailyService;

    /**
     * 更新今日数据
     * @param day
     * @return
     */
    @PostMapping("create/{day}")
    public Result createNowData(@PathVariable String day){
//        if(day.equals(DateUtil.format(DateUtil.date(),"yyyy-MM-dd"))) {
            boolean b = staticDailyService.createNowData(day);
            if (!b) {
                return Result.fail("生成失败");
            }
            return Result.succ(null);
//        }else{
//            return Result.fail("该日数据已存在");
//        }
    }

    /**
     * 图表显示 返回两部分数据：日期json数组、数量json数组
     */

    @GetMapping("showData/{type}/{begin}/{end}")
    public Result showData(@PathVariable String type,@PathVariable String begin,
                      @PathVariable String end){
        Map<String, Object> map = staticDailyService.getShowData(type,begin,end);
        return Result.succ(map);
    }

}
