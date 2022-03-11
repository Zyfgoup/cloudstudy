package com.zyfgoup.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.zyfgoup.entity.CmsAd;
import com.zyfgoup.entity.Result;
import com.zyfgoup.entity.dto.PageDTO;
import com.zyfgoup.entity.vo.AdVo;
import com.zyfgoup.service.CmsAdService;
import com.zyfgoup.utils.RedisKey;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 广告推荐 前端控制器
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-12
 */
@RestController
@RequestMapping("/ad")
public class CmsAdController {

    @Autowired
    private CmsAdService adService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping("save")
    public Result save(@RequestBody CmsAd ad) {
        CmsAd sort = adService.getOne(new QueryWrapper<CmsAd>().eq("sort", ad.getSort()));
        if(ObjectUtil.isNotNull(sort)){
            return Result.fail("该顺序已存在");
        }

        boolean result = adService.save(ad);
        if (result) {
            redisTemplate.delete(RedisKey.BANNER);
            return Result.succ("保存成功",null);
        } else {
            return Result.fail("保存失败");
        }
    }


    @PutMapping("update")
    public Result updateById(@RequestBody CmsAd ad) {
        boolean result = adService.updateById(ad);
        if (result) {
            redisTemplate.delete(RedisKey.BANNER);
            return Result.succ("修改成功",null);
        } else {
            return Result.fail("数据不存在");
        }
    }

    @GetMapping("get/{id}")
    public Result getById(@ApiParam(value = "推荐ID", required = true) @PathVariable String id) {
        CmsAd ad = adService.getById(id);
        if (ad != null) {
            return Result.succ(ad);
        } else {
            return Result.fail("数据不存在");
        }
    }

    @GetMapping("list/{page}/{limit}")
    public Result listPage(@PathVariable Long page,
                       @PathVariable Long limit) {

        IPage<AdVo> pageModel = adService.selectPage(page, limit);

        return Result.succ(PageDTO.get(pageModel));
    }

    @Transactional
    @DeleteMapping("remove/{id}")
    public Result removeById (@PathVariable String id) {

        //删除图片
        adService.removeAdImageById(id);

        //删除推荐
        boolean result = adService.removeById(id);
        if (result) {
            CmsAd ad = adService.getById(id);
            return Result.succ("删除成功");
        } else {
            return Result.fail("数据不存在");
        }
    }

}
