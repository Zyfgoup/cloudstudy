package com.zyfgoup.controller;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zyfgoup.client.VodClient;
import com.zyfgoup.entity.Course;
import com.zyfgoup.entity.CourseVideo;
import com.zyfgoup.entity.Result;
import com.zyfgoup.service.CourseService;
import com.zyfgoup.service.CourseVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 课程视频 但实际是在章节下面的 前端控制器
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-26
 */
@RestController
@RequestMapping("video")
public class CourseVideoController {

    @Autowired
    CourseService courseService;

    @Autowired
    CourseVideoService courseVideoService;

    @Autowired
    VodClient vodClient;

    /**
     * 添加课时
     * @param courseVideo
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody CourseVideo courseVideo){
        if(courseVideo==null || StrUtil.isBlank(courseVideo.getChapterId())||StrUtil.isBlank(courseVideo.getCourseId())){
            return Result.fail("参数或者课程、章节ID不能为空");
        }

        //查看是否超过总课时
        //一个视频为一个课时
        Course byId = courseService.getById(courseVideo.getCourseId());
        int count = courseVideoService.count(new QueryWrapper<CourseVideo>().eq("course_id", byId.getId()));
        if(count>=byId.getLessonNum()) {
            return Result.fail("添加失败,已达课程总课时");
        }

        //课时名称
        CourseVideo title = courseVideoService.getOne(new QueryWrapper<CourseVideo>().eq("title", courseVideo.getTitle())
        .eq("course_id",courseVideo.getCourseId()));
        if(ObjectUtil.isNotNull(title)) {
            return Result.fail("添加失败,课时名称已存在");
        }

        CourseVideo one = courseVideoService.getOne(new QueryWrapper<CourseVideo>().eq("sort", courseVideo.getSort())
                .eq("course_id", courseVideo.getCourseId()));
        if(ObjectUtil.isNotNull(one)) {
            return Result.fail("添加失败,课时排序已存在");
        }

        courseVideoService.save(courseVideo);
        return Result.succ("添加成功");
    }

    /**
     * 根据Id
     * @param id
     * @return
     */
    @GetMapping("/get/video/{id}")
    public Result getChapter(@PathVariable("id") String id){
        if(StrUtil.isBlank(id)) {
            return Result.fail("参数不能为空");
        }
        return Result.succ(courseVideoService.getById(id));
    }

    /**
     * 根据Id
     * @param
     * @return
     */
    @PutMapping("/upd")
    public Result upd(@RequestBody CourseVideo courseVideo){
        if(StrUtil.isBlank(courseVideo.getId())) {
            return Result.fail("id不能为空");
        }
        courseVideoService.updateById(courseVideo);
        return Result.succ("修改成功");
    }

    /**
     * 删除
     * @param
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") String id){
        if(StrUtil.isBlank(id)) {
            return Result.fail("参数不能为空");
        }

        //先查
        CourseVideo byId = courseVideoService.getById(id);

        //删除远程
        Result res = vodClient.removeVideo(byId.getVideoSourceId());

        if (res.getCode()==200) {
            //删除视频
            courseVideoService.removeById(id);
            return Result.succ("删除成功");
        }
        return Result.fail("删除失败");
    }

}
