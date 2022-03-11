package com.zyfgoup.controller.front;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zyfgoup.entity.Course;
import com.zyfgoup.entity.Result;
import com.zyfgoup.entity.Teacher;
import com.zyfgoup.service.CourseService;
import com.zyfgoup.service.TeacherService;
import com.zyfgoup.utils.RedisKey;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Api(description="前台首页：热门讲师；热门课程")
@RestController
@RequestMapping("/indexfront")
//@CrossOrigin
public class IndexFrontController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    StringRedisTemplate redisTemplate;

    //查询前4条名师
    @ApiOperation(value = "查询前4条名师")
    @GetMapping("indexHotTeacher")
    public Result indexHotTeacher() {
        if(redisTemplate.hasKey(RedisKey.HOT_Teacher)){
            List<Teacher> teachers = JSON.parseArray(redisTemplate.opsForValue().get(RedisKey.HOT_Teacher), Teacher.class);
            return Result.succ(teachers.subList(0,4));
        }
        List<Teacher> teacherList = teacherService.list(new QueryWrapper<Teacher>().orderByAsc("sort"));
        redisTemplate.opsForValue().set(RedisKey.HOT_Teacher,JSON.toJSONString(teacherList),1, TimeUnit.DAYS);
        return Result.succ(teacherList.subList(0,4));
    }

    //查询前8条热门课程
    @ApiOperation(value = "查询前8条热门课程")
    @GetMapping("indexHotCourse")
    public Result indexHotCourse() {
        if (redisTemplate.hasKey(RedisKey.HOT_COURSE)) {
            List<Course> courses = JSON.parseArray(redisTemplate.opsForValue().get(RedisKey.HOT_COURSE), Course.class);
            if(courses.size()>8){
                return Result.succ(courses.subList(0,8));
            }else
                return Result.succ(courses);
        } else{
            List<Course> courseList = courseService.list(new QueryWrapper<Course>().orderByDesc("buy_count"));
            redisTemplate.opsForValue().set(RedisKey.HOT_COURSE, JSON.toJSONString(courseList),2,TimeUnit.HOURS);
            if(courseList.size()>8){
                return Result.succ(courseList.subList(0,8));
            }else {
                return Result.succ(courseList);
            }
        }
    }

}
