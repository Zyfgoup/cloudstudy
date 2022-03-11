package com.zyfgoup.controller;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyfgoup.client.UserClient;
import com.zyfgoup.dto.PageDTO;
import com.zyfgoup.entity.Result;
import com.zyfgoup.entity.Teacher;
import com.zyfgoup.service.TeacherService;
import com.zyfgoup.utils.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-26
 */
@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    TeacherService teacherService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    UserClient userClient;


    /**
     * 添加
     * @param entity
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody Teacher entity){
        if(ObjectUtil.isNull(entity)){
            return Result.fail("参数不能为空");
        }

        Teacher sort = teacherService.getOne(new QueryWrapper<Teacher>().eq("sort", entity.getSort()));
        if(ObjectUtil.isNotNull(sort)){
            return Result.fail("无法添加,该讲师排序已存在");
        }

        boolean save = teacherService.save(entity);
        if(!save){
            return Result.fail("新增讲师失败");
        }
        //新增 删除key
        redisTemplate.delete(RedisKey.ALL_TEACHER);
        return Result.succ("新增成功",null);

    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable("id") String id){
        if(StrUtil.isBlank(id)){
            return Result.fail("参数不能为空");
        }
        Teacher byId = teacherService.getById(id);

        //删除头像
        userClient.removeAvaTar(byId.getAvatar());

        boolean save = teacherService.removeById(id);
        if(!save){
            return Result.fail("删除讲师失败");
        }
        redisTemplate.delete(RedisKey.ALL_TEACHER);
        return Result.succ("删除成功",null);

    }


    /**
     * list 根据名字 职称  时间搜索
     * @param
     * @return
     */
    @PostMapping("/get/{current}/{size}")
    public Result get(@PathVariable("current") Long current,@PathVariable("size") Long size,@RequestBody Map<String,String> searchObj){
        //Map
        String name =searchObj.get("name");
        String begin = searchObj.get("begin");
        String end = searchObj.get("end");
        String levelStr = searchObj.get("level");



        if(ObjectUtil.isNull(current)||ObjectUtil.isNull(size)){
            return Result.fail("分页参数不能为空");
        }

        if(current==0||size==0){
            return Result.fail("分页参数不能为0");
        }
        IPage page = new Page(current,size);
        QueryWrapper<Teacher> wrapper = new QueryWrapper<>();

        //名字
        wrapper.like(!StrUtil.isBlank(name), "name", name);
        //职称
        if(StrUtil.isNotBlank(levelStr)) {
            Integer level = Integer.valueOf(levelStr);
            wrapper.eq("level", level);
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //时间查询条件
        if(StrUtil.isNotBlank(begin)) {
            LocalDateTime startTime = LocalDateTime.parse(begin,dateTimeFormatter);
            wrapper.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + startTime + "')");
        }
        if(StrUtil.isNotBlank(end)) {
            LocalDateTime endTime = LocalDateTime.parse(end,dateTimeFormatter);
            wrapper.apply("UNIX_TIMESTAMP(create_time) < UNIX_TIMESTAMP('" + endTime + "')");
        }
        teacherService.page(page,wrapper);
        return Result.succ("查询成功",PageDTO.get(page));
    }

    /**
     * 修改
     * @param entity
     * @return
     */
    @PutMapping("/update")
    public Result update(@RequestBody Teacher entity){
        if(ObjectUtil.isNull(entity)){
            return Result.fail("参数不能为空");
        }

        boolean save = teacherService.updateById(entity);
        if(!save){
            return Result.fail("修改讲师信息失败");
        }
        //有更新 删除key
        redisTemplate.delete(RedisKey.ALL_TEACHER);
        return Result.succ("修改成功",null);

    }


    /**
     * 根据id查询
     * @param
     * @return
     */
    @GetMapping("/get/by/{id}")
    public Result getById(@PathVariable String id){
        if(StrUtil.isBlank(id)){
            return Result.fail("参数不能为空");
        }

        Teacher byId = teacherService.getById(id);
        if(ObjectUtil.isNull(byId)){
            return Result.fail("讲师信息为空");
        }
        return Result.succ("查询成功",byId);

    }

    /**
     * 获取所有讲师
     * @return
     */
    @GetMapping("/list")
    public Result list(){

        if(redisTemplate.hasKey(RedisKey.ALL_TEACHER)){
            return Result.succ(JSON.parseArray(redisTemplate.opsForValue().get(RedisKey.ALL_TEACHER),Teacher.class));
        }else {
            List<Teacher> list = teacherService.list(new QueryWrapper<Teacher>());
            //所有讲师 存到redis中
            redisTemplate.opsForValue().set(RedisKey.ALL_TEACHER, JSON.toJSONString(list),2,TimeUnit.HOURS);
            return Result.succ("查询成功", list);
        }
    }

}
