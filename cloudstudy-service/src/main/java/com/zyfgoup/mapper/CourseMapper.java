package com.zyfgoup.mapper;

import com.zyfgoup.entity.Course;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyfgoup.vo.CourseWebVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 Mapper 接口
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-26
 */
public interface CourseMapper extends BaseMapper<Course> {

    Map<String, Object> getMapById(String id);

    Map<String, Object> getMapByIdFront(String id);

    List<Map<String,Object>> getCategoryCourse();

    //根据课程id，查询课程信息
    CourseWebVo getBaseCourseInfo(String courseId);

}
