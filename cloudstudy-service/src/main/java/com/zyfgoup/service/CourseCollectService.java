package com.zyfgoup.service;

import com.zyfgoup.entity.CourseCollect;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zyfgoup.entity.vo.CourseCollectVo;

import java.util.List;

/**
 * <p>
 * 课程收藏 服务类
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-15
 */
public interface CourseCollectService extends IService<CourseCollect> {

    //获取课程收藏列表
    List<CourseCollectVo> selectByMemberId(String userId);

}
