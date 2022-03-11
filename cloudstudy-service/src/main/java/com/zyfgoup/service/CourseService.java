package com.zyfgoup.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyfgoup.entity.Course;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zyfgoup.vo.CourseFrontVo;
import com.zyfgoup.vo.CourseWebVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-26
 */
public interface CourseService extends IService<Course> {
    /**
     * 根据课程Id查询课程Map对象
     * @param id
     * @return
     */
    Map<String, Object> getMapById(String id);


    List<Map<String,Object>> getCategoryCourse();

    //条件查询带分页查询课程
    Map<String, Object> getCourseFrontList(Page<Course> pageCourse, CourseFrontVo courseFrontVo);

    //根据课程id，查询课程信息
    CourseWebVo getBaseCourseInfo(String courseId, HttpServletRequest request) throws InterruptedException;

     Map<String, Object> getCourseFreeFrontList(Page<Course> pageCourse, CourseFrontVo courseFron);
}
