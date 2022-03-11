package com.zyfgoup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zyfgoup.entity.Course;
import com.zyfgoup.entity.CourseChapter;
import com.zyfgoup.entity.CourseVideo;
import com.zyfgoup.entity.vo.OneChapter;
import com.zyfgoup.entity.vo.TwoVideo;
import com.zyfgoup.mapper.CourseChapterMapper;
import com.zyfgoup.service.CourseChapterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyfgoup.service.CourseVideoService;
import com.zyfgoup.vo.ChapterVo;
import com.zyfgoup.vo.VideoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程章节 服务实现类
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-26
 */
@Service
public class CourseChapterServiceImpl extends ServiceImpl<CourseChapterMapper, CourseChapter> implements CourseChapterService {

    @Autowired
    CourseVideoService courseVideoService;

    @Autowired
    CourseVideoService videoService;

    @Override
    public List<OneChapter> queryChapterAndVideoList(String id) {
        //定义一个章节集合
        List<OneChapter> oneChapterList = new ArrayList<>();
        QueryWrapper<CourseChapter> chapterWrapper = new QueryWrapper<>();
        chapterWrapper.eq("course_id",id);
        chapterWrapper.orderByAsc("sort", "id");
        //先查询章节列表集合
        List<CourseChapter> chapterList = baseMapper.selectList(chapterWrapper);
        //再遍历章节集合，获取每个章节ID
        for (CourseChapter chapter : chapterList) {
            OneChapter oneChapter = new OneChapter();
            BeanUtils.copyProperties(chapter,oneChapter);
            //再根据每个章节的ID查询节点的列表
            QueryWrapper<CourseVideo> videoWrapper = new QueryWrapper<>();
            videoWrapper.eq("chapter_id",oneChapter.getId());
            videoWrapper.orderByAsc("sort", "id");
            List<CourseVideo> eduVideoList = courseVideoService.list(videoWrapper);
            //把小节的列表添加章节中
            for(CourseVideo video : eduVideoList){
                TwoVideo twoVideo = new TwoVideo();
                BeanUtils.copyProperties(video,twoVideo);
                oneChapter.getChildren().add(twoVideo);
            }
            oneChapterList.add(oneChapter);
        }

        return oneChapterList;
    }

    @Override
    public List<ChapterVo> getChapterVideoByCourseId(String courseId) {
//1 根据课程id查询课程里面所有的章节
        QueryWrapper<CourseChapter> wrapperChapter = new QueryWrapper<>();
        wrapperChapter.eq("course_id",courseId);
        List<CourseChapter> eduChapters = baseMapper.selectList(wrapperChapter);

        //2 根据课程id查询课程里面所有的小节
        QueryWrapper<CourseVideo> wrapperVideo = new QueryWrapper<>();
        wrapperVideo.eq("course_id",courseId);
        List<CourseVideo> eduVideos = videoService.list(wrapperVideo);

        //创建list集合，用于最终封装数据
        List<ChapterVo> finalList = new ArrayList<>();

        //3 遍历查询章节list集合进行封装
        //遍历查询章节list集合
        for (int i = 0; i < eduChapters.size(); i++) {
            //得到每个章节
            CourseChapter eduChapter = eduChapters.get(i);
            //eduChapter对象值复制到ChapterVo里面
            ChapterVo chapterVo = new ChapterVo();
            BeanUtils.copyProperties(eduChapter,chapterVo);
            //把chapterVo放到最终list集合
            finalList.add(chapterVo);

            //创建集合，用于封装章节的小节
            List<VideoVo> videoList = new ArrayList<>();

            //4 遍历查询小节list集合，进行封装
            for (int m = 0; m < eduVideos.size(); m++) {
                //得到每个小节
                CourseVideo eduVideo = eduVideos.get(m);
                //判断：小节里面chapterid和章节里面id是否一样
                if(eduVideo.getChapterId().equals(eduChapter.getId())) {
                    //进行封装
                    VideoVo videoVo = new VideoVo();
                    BeanUtils.copyProperties(eduVideo, videoVo);
                    //放到小节封装集合
                    videoList.add(videoVo);
                }
            }

            //把封装之后小节list集合，放到章节对象里面
            chapterVo.setChildren(videoList);
        }
        return finalList;
    }
}
