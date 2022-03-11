package com.zyfgoup.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyfgoup.entity.CmsAdType;
import com.zyfgoup.entity.Result;
import com.zyfgoup.entity.dto.PageDTO;
import com.zyfgoup.service.CmsAdTypeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 推荐类型 前端控制器
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-12
 */
@RestController
@RequestMapping("/adType")
public class CmsAdTypeController {
    @Autowired
    private CmsAdTypeService adTypeService;

    @ApiOperation("所有推荐类别列表")
    @GetMapping("/list")
    public Result listAll() {
        List<CmsAdType> list = adTypeService.list(null);
        return Result.succ(list);
    }

    @ApiOperation("推荐类别分页列表")
    @GetMapping("list/{page}/{limit}")
    public Result listPage(@ApiParam(value = "当前页码", required = true) @PathVariable Long page,
                      @ApiParam(value = "每页记录数", required = true) @PathVariable Long limit) {

        Page<CmsAdType> pageParam = new Page<>(page, limit);
        IPage<CmsAdType> pageModel = adTypeService.page(pageParam,null);
        return Result.succ(PageDTO.get(pageModel));
    }

    @ApiOperation(value = "根据ID删除推荐类别")
    @DeleteMapping("remove/{id}")
    public Result removeById(@ApiParam(value = "推荐类别ID", required = true) @PathVariable String id) {

        boolean result = adTypeService.removeById(id);
        if (result) {
            return Result.succ("删除成功",null);
        } else {
            return Result.fail("删除失败");
        }
    }

    @ApiOperation("新增推荐类别")
    @PostMapping("/save")
    public Result save(@ApiParam(value = "推荐类别对象", required = true) @RequestBody CmsAdType adType) {

        boolean result = adTypeService.save(adType);
        if (result) {
            return Result.succ("添加成功",null);
        } else {
            return Result.fail("失败");
        }
    }

    @ApiOperation("更新推荐类别")
    @PutMapping("/update")
    public Result updateById(@ApiParam(value = "讲师推荐类别", required = true) @RequestBody CmsAdType adType) {
        boolean result = adTypeService.updateById(adType);
        if (result) {
            return Result.succ(null);
        } else {
            return Result.fail("更新失败");
        }
    }

    @ApiOperation("根据id获取推荐类别信息")
    @GetMapping("get/{id}")
    public Result getById(@ApiParam(value = "推荐类别ID", required = true) @PathVariable String id) {
        CmsAdType adType = adTypeService.getById(id);
        if (adType != null) {
            return Result.succ(adType);
        } else {
            return Result.fail("删除失败");
        }
    }

}
