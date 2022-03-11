package com.zyfgoup.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zyfgoup.entity.Course;
import com.zyfgoup.entity.Result;
import com.zyfgoup.entity.StaticDaily;
import com.zyfgoup.service.CourseService;
import com.zyfgoup.service.StaticDailyService;
import com.zyfgoup.utils.RedisKey;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author Zyfgoup
 * @Date 2021/3/11 17:36
 * @Description
 */
@RequestMapping("/chart")
@RestController
@Slf4j
public class ChartController {

    @Autowired
    StaticDailyService staticDailyService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    CourseService courseService;


    @PostMapping("/chart1")
    public Result chart1() {


        String today = DateUtil.format(DateUtil.date(), "yyyy-MM-dd");
        String yesterday = DateUtil.format(DateUtil.offsetDay(DateUtil.date(), -1), "yyyy-MM-dd");
        StaticDaily yes_data = staticDailyService.getOne(new QueryWrapper<StaticDaily>().eq("date_calculated", yesterday));

       //生成新的
        staticDailyService.createNowData(today);
        StaticDaily now_data = staticDailyService.getOne(new QueryWrapper<StaticDaily>().eq("date_calculated", today));

        if(ObjectUtil.isNull(yes_data) || ObjectUtil.isNull(now_data)){
            return Result.succ(null);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("yesterday", yesterday);
        map.put("today", today);

        List<Integer> yes = getData(yes_data);
        List<Integer> now = getData(now_data);
        map.put("yesData",yes);
        map.put("nowData",now);
        return Result.succ(map);

    }

    @PostMapping("/chart2")
    public Result chart2() {
        if(!redisTemplate.hasKey(RedisKey.SERVEN_DAY_DATA)){
            log.warn("7天数据暂无生成,访问首页自动生成");
            staticDailyService.create7Data();
            //return Result.fail();
        }
        String s = redisTemplate.opsForValue().get(RedisKey.SERVEN_DAY_DATA);
        Map<String, Object> res = JSON.parseObject(s);
        return Result.succ(res);

    }

    /**
     * 课程总购买前5
     * @return
     */
    @PostMapping("/chart3")
    public Result chart3(){

        if(redisTemplate.hasKey(RedisKey.TOP_5_BUY)){
            Map<String, Object> map = JSON.parseObject(redisTemplate.opsForValue().get(RedisKey.TOP_5_BUY));
            return Result.succ(map);
        }else {

            //前5个
            List<Course> bug_count = courseService.list(new QueryWrapper<Course>().orderByDesc("buy_count"));


            List<Course> courses = bug_count.subList(0, bug_count.size()>5?5:bug_count.size());

            List<String> name = new ArrayList<>();
            List<Long> bugCount = new ArrayList<>();

            for (int i = 0; i < courses.size(); i++) {
                name.add(courses.get(i).getTitle());
                bugCount.add(courses.get(i).getBuyCount());
            }
            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("buyCount", bugCount);
            //存到Redis中 30分钟删除
            redisTemplate.opsForValue().set(RedisKey.TOP_5_BUY, JSON.toJSONString(map), 30, TimeUnit.MINUTES);
            return Result.succ(map);
        }



    }

    /**
     * 分类饼图
     * @return
     */
    @PostMapping("/chart4")
    public Result chart4(){
        if(redisTemplate.hasKey(RedisKey.Category_COURSE)){
            Object parse = JSON.parse(redisTemplate.opsForValue().get(RedisKey.Category_COURSE));
            List<Map<String, Object>> list = (List<Map<String, Object>>) parse;
            return Result.succ(list);
        }else {

            //模拟的数据
            String s = "[{\"name\":\"GO\",\"value\":35},{\"name\":\"Java\",\"value\":120},{\"name\":\"C\",\"value\":11},{\"name\":\"数据结构与算法\",\"value\":66},{\"name\":\"操作系统\",\"value\":18},{\"name\":\"其他总和\",\"value\":214}]";

            List<Map<String, Object>> categoryCourse = courseService.getCategoryCourse();
            //redisTemplate.opsForValue().set(RedisKey.Category_COURSE, JSON.toJSONString(categoryCourse),1,TimeUnit.DAYS);
            redisTemplate.opsForValue().set(RedisKey.Category_COURSE, s,1,TimeUnit.DAYS);
            return Result.succ(categoryCourse);
        }
    }


    /**
     * 数据取出来装到list
     * @param staticDaily
     * @return
     */
    private List<Integer> getData(StaticDaily staticDaily){
        List<Integer> data = new ArrayList<>();
        data.add(staticDaily.getRegisterNum());
        data.add(staticDaily.getLoginNum());
        data.add(staticDaily.getVideoViewNum());
        data.add(staticDaily.getCourseNum());
        return data;
    }
}
