package com.zyfgoup.mapper;

import com.zyfgoup.entity.CourseCollect;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyfgoup.entity.vo.CourseCollectVo;

import java.util.List;

/**
 * <p>
 * 课程收藏 Mapper 接口
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-15
 */
public interface CourseCollectMapper extends BaseMapper<CourseCollect> {
    //获取课程收藏列表
    List<CourseCollectVo> selectByMemberId(String userId);

}
