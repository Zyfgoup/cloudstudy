package com.zyfgoup.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyfgoup.config.threadPoll.MyAsyncTask;
import com.zyfgoup.config.threadPoll.MyAsyncTask;
import com.zyfgoup.entity.Course;
import com.zyfgoup.entity.Result;
import com.zyfgoup.mapper.CourseMapper;
import com.zyfgoup.service.CourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyfgoup.vo.CourseFrontVo;
import com.zyfgoup.vo.CourseWebVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-26
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    MyAsyncTask asyncTask;


    @Override
    public Map<String, Object> getMapById(String id) {
        Map<String, Object> map = baseMapper.getMapById(id);
        return map;
    }

    @Override
    public List<Map<String, Object>> getCategoryCourse() {
        return baseMapper.getCategoryCourse();
    }

    @Override
    public Map<String, Object> getCourseFrontList(Page<Course> pageCourse, CourseFrontVo courseFrontVo){
// 根据讲师id查询所讲课程
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        //判断条件值是否为空，不为空拼接
        if(!StringUtils.isEmpty(courseFrontVo.getSubjectParentId())) { //一级分类
            wrapper.eq("category_parent_id",courseFrontVo.getSubjectParentId());
        }
        if(!StringUtils.isEmpty(courseFrontVo.getSubjectId())) { //二级分类
            wrapper.eq("category_id",courseFrontVo.getSubjectId());
        }
        if(!StringUtils.isEmpty(courseFrontVo.getBuyCountSort())) { //购买量
            wrapper.orderByDesc("buy_count");
        }

        if (!StringUtils.isEmpty(courseFrontVo.getGmtCreateSort())) { //最新
            wrapper.orderByDesc("create_time");
        }
        if (!StringUtils.isEmpty(courseFrontVo.getPriceSort())) {//价格
            wrapper.orderByDesc("price");
        }

        //模糊搜索
        wrapper.like(!StrUtil.isBlank(courseFrontVo.getTitle()),"title",courseFrontVo.getTitle());

        //默认返回浏览降序排序
        wrapper.orderByDesc("view_count");

        wrapper.eq("status","Normal");

        baseMapper.selectPage(pageCourse,wrapper);

        List<Course> records = pageCourse.getRecords();
        long current = pageCourse.getCurrent();
        long pages = pageCourse.getPages();
        long size = pageCourse.getSize();
        long total = pageCourse.getTotal();
        boolean hasNext = pageCourse.hasNext();//下一页
        boolean hasPrevious = pageCourse.hasPrevious();//上一页

        //把分页数据获取出来，放到map集合
        Map<String, Object> map = new HashMap<>();
        map.put("items", records);
        map.put("current", current);
        map.put("pages", pages);
        map.put("size", size);
        map.put("total", total);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);

        //map返回
        return map;
    }

    @Override
    public Map<String, Object> getCourseFreeFrontList(Page<Course> pageCourse, CourseFrontVo courseFrontVo){
// 根据讲师id查询所讲课程
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        //判断条件值是否为空，不为空拼接
        if(!StringUtils.isEmpty(courseFrontVo.getSubjectParentId())) { //一级分类
            wrapper.eq("category_parent_id",courseFrontVo.getSubjectParentId());
        }
        if(!StringUtils.isEmpty(courseFrontVo.getSubjectId())) { //二级分类
            wrapper.eq("category_id",courseFrontVo.getSubjectId());
        }
        if(!StringUtils.isEmpty(courseFrontVo.getBuyCountSort())) { //关注度
            wrapper.orderByDesc("buy_count");
        }
        if (!StringUtils.isEmpty(courseFrontVo.getGmtCreateSort())) { //最新
            wrapper.orderByDesc("create_time");
        }

        wrapper.orderByDesc("view_count");

        wrapper.eq("price",0);

        //模糊搜索
        wrapper.like(!StrUtil.isBlank(courseFrontVo.getTitle()),"title",courseFrontVo.getTitle());


        baseMapper.selectPage(pageCourse,wrapper);

        List<Course> records = pageCourse.getRecords();
        long current = pageCourse.getCurrent();
        long pages = pageCourse.getPages();
        long size = pageCourse.getSize();
        long total = pageCourse.getTotal();
        boolean hasNext = pageCourse.hasNext();//下一页
        boolean hasPrevious = pageCourse.hasPrevious();//上一页

        //把分页数据获取出来，放到map集合
        Map<String, Object> map = new HashMap<>();
        map.put("items", records);
        map.put("current", current);
        map.put("pages", pages);
        map.put("size", size);
        map.put("total", total);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);

        //map返回
        return map;
    }

    //根据课程id，查询课程信息，更新浏览量
    @Override
    public CourseWebVo getBaseCourseInfo(String courseId, HttpServletRequest request) throws InterruptedException {
        //开启线程更新浏览数
        asyncTask.courseViewAdd(courseId,request);
        //获取课程信息
        return baseMapper.getBaseCourseInfo(courseId);
    }


}
