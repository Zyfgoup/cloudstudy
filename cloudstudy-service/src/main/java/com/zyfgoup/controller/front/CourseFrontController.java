package com.zyfgoup.controller.front;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyfgoup.entity.Course;
import com.zyfgoup.entity.CourseWebVoOrder;
import com.zyfgoup.entity.Result;
import com.zyfgoup.service.CourseChapterService;
import com.zyfgoup.service.CourseService;
import com.zyfgoup.utils.RedisKey;
import com.zyfgoup.vo.ChapterVo;
import com.zyfgoup.vo.CourseFrontVo;
import com.zyfgoup.vo.CourseWebVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Api(description="课程页功能")
@RestController
@RequestMapping("/coursefront")
public class CourseFrontController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseChapterService chapterService;

    @Autowired
    StringRedisTemplate redisTemplate;


    @ApiOperation(value = "条件查询带分页查询课程")
    @PostMapping("/getFrontCourseList/{page}/{limit}")
    public Result getFrontCourseList(@PathVariable("page") long page,@PathVariable("limit") long limit,@RequestBody CourseFrontVo courseFrontVo) {
        Page<Course> pageCourse = new Page<>(page, limit);
        Map<String, Object> map = courseService.getCourseFrontList(pageCourse, courseFrontVo);
        //返回分页所有数据
        return Result.succ(map);
    }

    @ApiOperation(value = "条件查询带分页查询免费课程")
    @PostMapping("/getFrontCourseList/free/{page}/{limit}")
    public Result getFrontFreeCourseList(@PathVariable("page") long page,@PathVariable("limit") long limit,@RequestBody CourseFrontVo courseFrontVo) {
        Page<Course> pageCourse = new Page<>(page, limit);
        Map<String, Object> map = courseService.getCourseFreeFrontList(pageCourse, courseFrontVo);
        //返回分页所有数据
        return Result.succ(map);
    }

    //2 课程详情的方法
    @ApiOperation(value = " 课程详情的方法")
    @GetMapping("getFrontCourseInfo/{courseId}")
    public Result getFrontCourseInfo(@PathVariable String courseId, HttpServletRequest request) throws InterruptedException {
        //根据课程id，编写sql语句查询课程信息
        CourseWebVo courseWebVo = courseService.getBaseCourseInfo(courseId,request);
        //根据课程id查询章节和小节
        List<ChapterVo> chapterVideoList = chapterService.getChapterVideoByCourseId(courseId);
        Map<String, Object> map = new HashMap<>();
        map.put("courseWebVo",courseWebVo);
        map.put("chapterVideoList",chapterVideoList);
        return Result.succ(map);
    }

    //根据课程id查询课程信息
    @ApiOperation(value = " 根据课程id查询课程信息")
    @PostMapping("getCourseInfoOrder/{id}")
    public CourseWebVoOrder getCourseInfoOrder(@PathVariable String id,HttpServletRequest request) throws InterruptedException {
        CourseWebVo courseInfo = courseService.getBaseCourseInfo(id,request);
        CourseWebVoOrder courseWebVoOrder = new CourseWebVoOrder();
        BeanUtils.copyProperties(courseInfo,courseWebVoOrder);
        return courseWebVoOrder;
    }
}












