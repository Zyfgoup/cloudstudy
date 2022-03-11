package com.zyfgoup.service;

import com.zyfgoup.entity.StaticDaily;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 网站统计日数据 服务类
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-10
 */
public interface StaticDailyService extends IService<StaticDaily> {
    //统计某一天的注册人数,生成统计数据
    boolean createNowData(String day);

    //图表显示 返回两部分数据：日期json数组、数量json数组
    Map<String,Object> getShowData(String type, String begin, String end);


    void create7Data();
}
