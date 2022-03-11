package com.zyfgoup.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyfgoup.entity.CmsAd;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zyfgoup.entity.vo.AdVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 广告推荐 Mapper 接口
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-12
 */
public interface CmsAdMapper extends BaseMapper<CmsAd> {
    List<AdVo> selectPageByQueryWrapper(
            Page<AdVo> pageParam,
            @Param(Constants.WRAPPER) QueryWrapper<AdVo> queryWrapper);



}
