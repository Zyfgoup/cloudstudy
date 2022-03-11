package com.zyfgoup.service.impl;

import com.zyfgoup.entity.CourseVideo;
import com.zyfgoup.mapper.CourseVideoMapper;
import com.zyfgoup.service.CourseVideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 课程视频 但实际是在章节下面的 服务实现类
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-26
 */
@Service
public class CourseVideoServiceImpl extends ServiceImpl<CourseVideoMapper, CourseVideo> implements CourseVideoService {

}
