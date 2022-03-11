package com.zyfgoup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyfgoup.client.FileClient;
import com.zyfgoup.entity.CmsAd;
import com.zyfgoup.entity.Result;
import com.zyfgoup.entity.vo.AdVo;
import com.zyfgoup.mapper.CmsAdMapper;
import com.zyfgoup.service.CmsAdService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 广告推荐 服务实现类
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-12
 */
@Service
public class CmsAdServiceImpl extends ServiceImpl<CmsAdMapper, CmsAd> implements CmsAdService {

    @Autowired
    private FileClient fileClient;

    @Override
    public IPage<AdVo> selectPage(Long page, Long limit) {

        QueryWrapper<AdVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("a.type_id", "a.sort");

        Page<AdVo> pageParam = new Page<>(page, limit);

        List<AdVo> records = baseMapper.selectPageByQueryWrapper(pageParam, queryWrapper);
        pageParam.setRecords(records);
        return pageParam;
    }

    @Override
    public boolean removeAdImageById(String id) {
        CmsAd ad = baseMapper.selectById(id);
        if(ad != null) {
            String imagesUrl = ad.getImageUrl();
            if(!StringUtils.isEmpty(imagesUrl)){
                //删除图片
                Result r = fileClient.removeFile(imagesUrl);
                if(r.getCode() == 200){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<CmsAd> selectByAdTypeId(String adTypeId) {
        QueryWrapper<CmsAd> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort", "id");
        queryWrapper.eq("type_id", adTypeId);
        return baseMapper.selectList(queryWrapper);
    }


}
