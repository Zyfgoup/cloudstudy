package com.zyfgoup.service;

import com.zyfgoup.entity.CourseChapter;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zyfgoup.entity.vo.OneChapter;
import com.zyfgoup.vo.ChapterVo;

import java.util.List;

/**
 * <p>
 * 课程章节 服务类
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-26
 */
public interface CourseChapterService extends IService<CourseChapter> {

   List<OneChapter> queryChapterAndVideoList(String id);

   //课程大纲列表,根据课程id进行查询
   List<ChapterVo> getChapterVideoByCourseId(String courseId);

}
