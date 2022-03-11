package com.zyfgoup.controller;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zyfgoup.client.VodClient;
import com.zyfgoup.entity.CourseChapter;
import com.zyfgoup.entity.CourseVideo;
import com.zyfgoup.entity.Result;
import com.zyfgoup.entity.vo.OneChapter;
import com.zyfgoup.service.CourseChapterService;
import com.zyfgoup.service.CourseVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程章节 前端控制器
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-26
 */
@RestController
@RequestMapping("/chapter")
public class CourseChapterController {
    @Autowired
    CourseChapterService courseChapterService;

    @Autowired
    CourseVideoService courseVideoService;

    @Autowired
    VodClient vodClient;


    @PostMapping("/add")
    public Result add(@RequestBody CourseChapter courseChapter){
        if(courseChapter==null || StrUtil.isBlank(courseChapter.getCourseId())){
            return Result.fail("参数或者课程ID不能为空");
        }
        CourseChapter title = courseChapterService.getOne(new QueryWrapper<CourseChapter>().eq("title", courseChapter.getTitle()));
        if(ObjectUtil.isNotNull(title)){
            return Result.fail("章节名已存在");
        }

        CourseChapter sort = courseChapterService.getOne(new QueryWrapper<CourseChapter>().eq("sort", courseChapter.getSort())
        .eq("course_id",courseChapter.getCourseId()));
        if(ObjectUtil.isNotNull(sort)){
            return Result.fail("章节排序已存在");
        }

        courseChapterService.save(courseChapter);



        return Result.succ("添加成功",courseChapter);
    }

    /**
     * 根据courseId 查所有的章节和视频
     * @param id
     * @return
     */
    @GetMapping("/get/{id}")
    public Result get(@PathVariable("id") String id){
        List<OneChapter> list = courseChapterService.queryChapterAndVideoList(id);
        if(list.size()>=0){
            return Result.succ(list);
        }
        return Result.fail("查询失败");
    }

    /**
     * 根据chapterId
     * @param id
     * @return
     */
    @GetMapping("/get/chapter/{id}")
    public Result getChapter(@PathVariable("id") String id){
       if(StrUtil.isBlank(id)) {
           return Result.fail("参数不能为空");
       }
       return Result.succ(courseChapterService.getById(id));
    }

    /**
     * 更新
     * @param
     * @return
     */
    @PutMapping("/upd")
    public Result upd(@RequestBody CourseChapter courseChapter){
        if(courseChapter==null || StrUtil.isBlank(courseChapter.getId())|| StrUtil.isBlank(courseChapter.getCourseId())) {
            return Result.fail("参数不能为空");
        }
        courseChapterService.updateById(courseChapter);
        return Result.succ("更新成功");
    }

    /**
     * 删除
     * @param
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") String chapterId){
        if(StrUtil.isBlank(chapterId)) {
            return Result.fail("参数不能为空");
        }

        //删除章节下的video
        //要拿到对应的sourceId
        //判断是否有课时先 如果大小为0 则不进行删除
        List<CourseVideo> chapter_id = courseVideoService.list(new QueryWrapper<CourseVideo>().eq("chapter_id", chapterId));
        List<String> listSourceId = new ArrayList<>();
        chapter_id.stream().forEach(video -> {
            listSourceId.add(video.getVideoSourceId());
        });

        Result result = vodClient.removeVideoList(listSourceId);
        if(result.getCode()==200) {

            courseVideoService.remove(new QueryWrapper<CourseVideo>().eq("chapter_id", chapterId));
            //删除章节
            courseChapterService.removeById(chapterId);
            return Result.succ("删除成功");
        }
        return Result.fail("删除失败");
    }


}
