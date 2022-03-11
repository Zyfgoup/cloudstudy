package com.zyfgoup.schedule;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zyfgoup.entity.Course;
import com.zyfgoup.entity.CourseLike;
import com.zyfgoup.entity.StaticDaily;
import com.zyfgoup.service.CourseLikeService;
import com.zyfgoup.service.CourseService;
import com.zyfgoup.service.StaticDailyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Author Zyfgoup
 * @Date 2021/2/3 16:36
 * @Description
 */
@Component
@Slf4j
public class Schedule {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    CourseService courseService;

    @Autowired
    CourseLikeService courseLikeService;

    @Autowired
    StaticDailyService staticDailyService;

    //cron表达式  设置时间 spring写6位，不然会报错
    // 在线生成cron表达式  http://cron.qqe2.com/
    //在每天0点，把前一天数据进行数据查询添加
    @Scheduled(cron = "0 0 0 * * ? ")
    public void taskDaily() {
        log.info("生成昨日的数据以及前面7天的数据封装成生成Echart");
        boolean remove = staticDailyService.remove(new QueryWrapper<StaticDaily>().eq("date_calculated", DateUtil.date()));
        if(remove) {
            staticDailyService.createNowData(DateUtil.formatDate(com.zyfgoup.util.DateUtil.addDays(new Date(), -1)));
            staticDailyService.create7Data();
        }else{
            log.error("生成昨日数据失败 原因：先删除昨日已有数据失败");
        }
        staticDailyService.create7Data();
    }


    /**
     * 每隔半小时
     * 将每个课程的访问加到总访问量上
     * 删除ip
     * 维护一个昨天前五名的view_rank
     * 删除view
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void courseView(){
        log.info("开始将view持久化到mysql");
        Set<Object> view = redisTemplate.opsForHash().keys("view");
        for (Object obj : view) {
            String courseId = (String)obj;
            Long viewNum = Long.parseLong((String)redisTemplate.opsForHash().get("view", courseId));

            log.info("课程id："+courseId+", 昨日viewNum: "+viewNum);
            courseService.update(new UpdateWrapper<Course>().eq("id",courseId).setSql("view_count = view_count +"+viewNum));

            //维护一个view_rank
            redisTemplate.opsForZSet().incrementScore("view_rank",courseId,viewNum);
        }

        //删除key
        redisTemplate.delete("ip");
        redisTemplate.delete("view");

        log.info("ip、view删除成功,view持久化成功");


    }

    /**
     *
     * 将每个课程的购买持久化到mysql
     * 维护一个昨天前五名的bug_rank
     * 删除bug
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    public void courseBug(){
        log.info("开始将buy持久化到mysql");
        Set<Object> like = redisTemplate.opsForHash().keys("buy");
        for (Object obj : like) {
            String courseId = (String)obj;
            Long buyNum = Long.parseLong((String)(redisTemplate.opsForHash().get("buy", courseId)));

            log.info("课程id："+courseId+", 昨日buyNum: "+buyNum);
            courseService.update(new UpdateWrapper<Course>().eq("id",courseId).setSql("buy_count = buy_count +"+buyNum));

            //维护一个rank
            redisTemplate.opsForZSet().incrementScore("buy_rank",courseId,buyNum);
        }

        //删除key
        redisTemplate.delete("buy");

        log.info("buy删除成功,buy持久化成功");


    }


    /**
     * 保存昨日的浏览和购买排行
     */
    @Scheduled(cron = "0 0 0 * * ? ")
    public void SaveViewAndBuyRank() {
        /**
         * 。。。
         */
        redisTemplate.delete("view_rank");
        redisTemplate.delete("buy_rank");
    }





}
