package com.zyfgoup.controller;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyfgoup.dto.PageDTO;
import com.zyfgoup.entity.Category;
import com.zyfgoup.entity.Result;
import com.zyfgoup.exceImpl.DelCategoryException;
import com.zyfgoup.service.CategoryService;
import com.zyfgoup.utils.RedisKey;
import com.zyfgoup.vo.CategoryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 分类 前端控制器
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-26
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @Autowired
    StringRedisTemplate redisTemplate;


    /**
     * 根据文档导入
     * @param file
     * @return
     */
    @PostMapping("/import")
    public Result importCategory(MultipartFile file){
        //上传过来excel文件
        categoryService.importCategory(file,categoryService);
        redisTemplate.delete(RedisKey.ALL_CATEGORY);
        return Result.succ(null);

    }


    /**
     * 新增分类
     * @param category
     * @return
     */

    @PostMapping("/add")
    public Result add(@RequestBody Category category){
       if (ObjectUtil.isNull(category)){
            return Result.fail("添加失败,信息为空");
        }

        boolean save = categoryService.save(category);
        if(!save){
            return Result.fail("新增分类失败");
        }

        //新增成功后 删除
        redisTemplate.delete(RedisKey.ALL_CATEGORY);

        return Result.succ("新增分类成功",null);

    }


    /**
     * 获取所有分类
     * @return
     */
    @GetMapping("/get/all")
    public Result getAll(){

        //redis存在则直接返回
        if(redisTemplate.hasKey(RedisKey.ALL_CATEGORY)){
            List<CategoryVO> list = JSON.parseObject(redisTemplate.opsForValue().get(RedisKey.ALL_CATEGORY),List.class);
            return Result.succ("查询成功",list);
        }

        //不存在走 mysql
        List<Category> parentCategories = categoryService.list(new QueryWrapper<Category>().eq("parent_id","0"));
        List<Category> childrenCategories = categoryService.list(new QueryWrapper<Category>().ne("parent_id","0"));
        List<CategoryVO> list = new ArrayList<>();
        //遍历父
        parentCategories.stream().forEach(parent->{
            CategoryVO categoryVO = new CategoryVO();

            //找到对应的子分类
            Stream<Category> stream = childrenCategories.stream().filter(category ->
                     category.getParentId().equals(parent.getId()));

            //转化为集合
            List<Category> children = stream.collect(Collectors.toList());


            //构建VO
            BeanUtils.copyProperties(parent,categoryVO);
            categoryVO.setChildren(children);
            list.add(categoryVO);
        });

        //放到redis中
        redisTemplate.opsForValue().set(RedisKey.ALL_CATEGORY, JSON.toJSONString(list),1, TimeUnit.HOURS);

        return Result.succ("查询成功",list);
    }

    @DeleteMapping("/delete/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result delete(@PathVariable("id") String id) throws DelCategoryException {
        if (StringUtils.isEmpty(id)){
            return Result.fail("删除失败,id为空");
        }
        //如果删除父级目录 那么下面的子分类都要删除
        boolean flag = true;
        //链式写法 如果id找不到 那么就空指针异常了
        //得先判断是否能找到
        Category byId = categoryService.getById(id);
        if(ObjectUtil.isNull(byId)){
            return Result.fail("删除失败,id不存在");
        }
        if("0".equals(byId.getParentId())){
            flag = categoryService.remove(new QueryWrapper<Category>().eq("parent_id",id));
        }

        //删除
        boolean flag1 = categoryService.removeById(id);
        if(!flag||!flag1){
            throw new DelCategoryException();
        }

        //删除成功后 删除redis
       redisTemplate.delete(RedisKey.ALL_CATEGORY);
        return  Result.succ("删除分类成功",null);
    }


    /**
     * 排序 0-10
     * @param map
     * @return
     */
    @PostMapping("/upd/sort")
    public Result updSort(@RequestBody Map<String,Object> map){
        String id = (String)map.get("id");
        Object obj = map.get("sort");
        Integer sort = null;

        //兼容传入的sort是字符串或者是Number
        if(obj instanceof String){
            sort = Integer.valueOf((String)obj);
        }else if(obj instanceof Integer){
            sort = (Integer)obj;
        }

        if(StrUtil.isBlank(id)||ObjectUtil.isNull(sort)){
            return Result.fail("参数不能为空");
        }
        if(sort<0||sort>10){
            return Result.fail("排序权重范围为0~10");
        }

        boolean update = categoryService.update(new UpdateWrapper<Category>().eq("id", id).set("sort", sort));
        if(!update){
            return Result.fail("修改失败");
        }
        return Result.succ("修改成功",null);

    }

}
