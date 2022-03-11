package com.zyfgoup.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyfgoup.entity.Course;
import com.zyfgoup.entity.CourseCollect;
import com.zyfgoup.entity.vo.CourseCollectVo;
import com.zyfgoup.mapper.CourseCollectMapper;
import com.zyfgoup.service.CourseCollectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyfgoup.vo.CourseFrontVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程收藏 服务实现类
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-15
 */
@Service
public class CourseCollectServiceImpl extends ServiceImpl<CourseCollectMapper, CourseCollect> implements CourseCollectService {

    @Override
    public List<CourseCollectVo> selectByMemberId(String userId) {
        return baseMapper.selectByMemberId(userId);
    }

}
