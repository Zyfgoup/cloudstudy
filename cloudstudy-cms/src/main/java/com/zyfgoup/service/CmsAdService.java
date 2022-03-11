package com.zyfgoup.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zyfgoup.entity.CmsAd;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zyfgoup.entity.vo.AdVo;

import java.util.List;

/**
 * <p>
 * 广告推荐 服务类
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-12
 */
public interface CmsAdService extends IService<CmsAd> {
    IPage<AdVo> selectPage(Long page, Long limit);

    boolean removeAdImageById(String id);

    List<CmsAd> selectByAdTypeId(String adTypeId);

}
