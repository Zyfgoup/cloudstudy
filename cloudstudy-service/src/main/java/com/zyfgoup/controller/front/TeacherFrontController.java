package com.zyfgoup.controller.front;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyfgoup.entity.Course;
import com.zyfgoup.entity.Result;
import com.zyfgoup.entity.Teacher;
import com.zyfgoup.service.CourseService;
import com.zyfgoup.service.TeacherService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/teacherfront")
//@CrossOrigin
public class TeacherFrontController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private CourseService courseService;

    //1 分页查询讲师的方法
    @ApiOperation(value = "分页查询讲师的方法")
    @PostMapping("getTeacherFrontList/{page}/{limit}/{teacherName}")
    public Result getTeacherFrontList(@PathVariable long page, @PathVariable long limit,@PathVariable String teacherName) {

            String name = (String) JSON.parse(teacherName);
        Page<Teacher> pageTeacher = new Page<>(page,limit);

        QueryWrapper<Teacher> wrapper = new QueryWrapper<>();

        //名字模糊搜索
        wrapper.like(!StrUtil.isBlank(name),"name",name);
        wrapper.orderByAsc("sort");
        //把分页数据封装到pageTeacher对象里去
        teacherService.page(pageTeacher,wrapper);

        List<Teacher> records = pageTeacher.getRecords();
        long current = pageTeacher.getCurrent();
        long pages = pageTeacher.getPages();
        long size = pageTeacher.getSize();
        long total = pageTeacher.getTotal();
        boolean hasNext = pageTeacher.hasNext();//下一页
        boolean hasPrevious = pageTeacher.hasPrevious();//上一页

        //把分页数据获取出来，放到map集合
        Map<String, Object> map = new HashMap<>();
        map.put("items", records);
        map.put("current", current);
        map.put("pages", pages);
        map.put("size", size);
        map.put("total", total);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);
        //返回分页所有数据
        return Result.succ(map);
    }

    //2 讲师详情的功能
    @ApiOperation(value = "讲师详情的功能")
    @GetMapping("getTeacherFrontInfo/{teacherId}")
    public Result getTeacherFrontInfo(@PathVariable String teacherId) {
        //1 根据讲师id查询讲师基本信息
        Teacher eduTeacher = teacherService.getById(teacherId);
        //2 根据讲师id查询所讲课程
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.eq("teacher_id",teacherId);
        List<Course> courseList = courseService.list(wrapper);
        Map<String, Object> map = new HashMap<>();
        map.put("teacher",eduTeacher);
        map.put("courseList",courseList);
        return Result.succ(map);
    }
}












