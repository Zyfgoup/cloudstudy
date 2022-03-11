package com.zyfgoup.controller;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyfgoup.client.OrderClient;
import com.zyfgoup.client.UserClient;
import com.zyfgoup.client.VodClient;
import com.zyfgoup.dto.PageDTO;
import com.zyfgoup.entity.*;
import com.zyfgoup.service.CourseChapterService;
import com.zyfgoup.service.CourseCollectService;
import com.zyfgoup.service.CourseService;
import com.zyfgoup.service.CourseVideoService;
import com.zyfgoup.utils.RedisKey;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-26
 */
@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    CourseService courseService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    CourseChapterService courseChapterService;

    @Autowired
    CourseVideoService courseVideoService;

    @Autowired
    VodClient vodClient;

    @Autowired
    UserClient userClient;

    @Autowired
    CourseCollectService courseCollectService;

    @Autowired
    OrderClient orderClient;



    /**
     * 分页查询
     * @param current
     * @param size
     * @param searchObj
     * @return
     */
    @PostMapping("/get/{current}/{size}")
    public Result get(@PathVariable("current") Long current,@PathVariable("size") Long size,@RequestBody Map<String,String> searchObj){
        if(ObjectUtil.isNull(current)||ObjectUtil.isNull(size)){
            return Result.fail("分页参数不能为空");
        }

        if(current==0||size==0){
            return Result.fail("分页参数不能为0");
        }

        //Map
        String cParentId =searchObj.get("categoryParentId");
        String cId = searchObj.get("categoryId");
        String title = searchObj.get("title");
        String tId = searchObj.get("teacherId");



        //如果查询条件都为空
        //从redis
        if(StrUtil.isBlank(cParentId) && StrUtil.isBlank(cId) && StrUtil.isBlank(title) && StrUtil.isBlank(tId)){
            if(!redisTemplate.hasKey(RedisKey.ALL_COURSE)) {
                List<Course> list = courseService.list();
                redisTemplate.opsForValue().set(RedisKey.ALL_COURSE, JSON.toJSONString(list),1, TimeUnit.HOURS);
            }

            //都从redis拿
            String s = redisTemplate.opsForValue().get(RedisKey.ALL_COURSE);
            List<Course> list = JSON.parseArray(s,Course.class);

            // 要保证 尾部不能超过已有条数
            int start = (int)(current-1)*10;
            int end = (current*size)>list.size()?list.size():(int)(current*size);
            //取多少到多少 从0开始 左闭右开
            List<Course> courses = list.subList(start,end);
            PageDTO pageDTO = new PageDTO(courses,current,size,Long.valueOf(list.size()));
            return Result.succ(pageDTO);
        }

        //有查询条件
        //构建分页
        IPage page = new Page(current,size);
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(cParentId),"category_parent_id",cParentId);
        wrapper.eq(StrUtil.isNotBlank(cId),"category_id",cId);
        wrapper.like(StrUtil.isNotBlank(title),"title",title);
        wrapper.eq(StrUtil.isNotBlank(tId),"teacher_id",tId);

        courseService.page(page,wrapper);
        return Result.succ(PageDTO.get(page));
    }


    @PostMapping("/list")
    public Result list(){

            //从redis
            if(!redisTemplate.hasKey(RedisKey.ALL_COURSE)) {
                List<Course> list = courseService.list();
                redisTemplate.opsForValue().set(RedisKey.ALL_COURSE, JSON.toJSONString(list),1, TimeUnit.HOURS);
            }

            //都从redis拿
            String s = redisTemplate.opsForValue().get(RedisKey.ALL_COURSE);
            List<Course> list = JSON.parseArray(s,Course.class);

            return Result.succ(list);
        }


    /**
     * 删除
     * 视频点播要钱  测试的时候经常传来传去
     * 这里默认删除远程视频成功 然后继续走
     * @param
     * @return
     */
    @Transactional
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable("id") String id){
        if(StrUtil.isBlank(id)) {
            return Result.fail("参数不能为空");
        }


        // TODO: 2021/3/12 测试 视频没那么多 删除课程时不删除视频
        //删除课程下的所有video
//           List<CourseVideo> videos = courseVideoService.list(new QueryWrapper<CourseVideo>().eq("course_id", id));
//            if(videos.size()>0) {
//               List<String> listSourceId = new ArrayList<>();
//               videos.stream().forEach(video -> {
//                   listSourceId.add(video.getVideoSourceId());
//               });
//
//                Result result = vodClient.removeVideoList(listSourceId);
//                if(result.getCode() != 200) {
//                    //如果删除失败 则抛出异常 不继续执行了
//                    throw new DelCourseException();
//                }
//           }


                Course byId = courseService.getById(id);
                //删除封面
                userClient.removeAvaTar(byId.getCover());
                
                 //删除课程
                courseService.removeById(id);
                //删除章节
                courseChapterService.remove(new QueryWrapper<CourseChapter>().eq("course_id",id));
                //删除课时
                courseVideoService.remove(new QueryWrapper<CourseVideo>().eq("course_id",id));

                //删除课程的收藏
                courseCollectService.remove(new QueryWrapper<CourseCollect>().eq("course_id",id));

                //删除购物车
                orderClient.deleteCartByCourseId(id);

                redisTemplate.delete(RedisKey.ALL_COURSE);
                return Result.succ("删除成功");

    }


    /**
     * 根据id获取课程信息
     * @param id
     * @return
     */
    @GetMapping("/get/{id}")
    public Result get(@PathVariable("id") String id){

        if(StrUtil.isBlank(id)){
            return Result.fail("参数不能为空");
        }

        return Result.succ("查询成功", courseService.getById(id));
    }


    /**
     * 添加课程
     * @param course
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody Course course){
        if(ObjectUtil.isNull(course)){
            return Result.fail("参数不能为空");
        }

        Course title = courseService.getOne(new QueryWrapper<Course>().eq("title", course.getTitle()));
        if(ObjectUtil.isNotNull(title)){
            return Result.fail("课程名已存在");
        }

        boolean save = courseService.save(course);
        if(!save){
            return Result.fail("新增课程失败");
        }

        //返回课程id
        redisTemplate.delete(RedisKey.ALL_COURSE);
        return Result.succ("新增课程成功",course.getId());
    }


    /**
     * 分页获取免费的视频
     * @param current
     * @param size
     * @return
     */
    @GetMapping("/get/free/{current}/{size}")
    public Result getFree(@PathVariable("current") Long current,@PathVariable("size") Long size){
        if(ObjectUtil.isNull(current)||ObjectUtil.isNull(size)){
            return Result.fail("参数不能为空");
        }

        if(current==0||size==0){
            return Result.fail("分页参数不能为0");
        }

        IPage page = new Page(current,size);
        courseService.page(page,new QueryWrapper<Course>().eq("price",BigDecimal.ZERO));


        return Result.succ("获取免费视频成功", PageDTO.get(page));
    }



    /**
     * 修改课程信息
     */
    @PutMapping("/upd")
    public Result upd(@RequestBody Course course){
        if(ObjectUtil.isNull(course)||StrUtil.isBlank(course.getId())){
            return Result.fail("参数不能为空");
        }
        boolean b = courseService.updateById(course);
        if(!b){
            return Result.fail("更新课程信息失败");
        }
        redisTemplate.delete(RedisKey.ALL_COURSE);
        return Result.succ("更新课程信息成功",null);
    }

    /**
     * 发布 修改状态
     */
    @PutMapping("/updateStatus/{id}")
    public Result upd(@PathVariable("id") String courseId){
        if(StrUtil.isBlank(courseId)){
            return Result.fail("参数不能为空");
        }
        boolean update = courseService.update(new UpdateWrapper<Course>().eq("id", courseId)
                .set("status", "Normal"));
        if(!update){
            return Result.fail("发布失败");
        }
        redisTemplate.delete(RedisKey.ALL_COURSE);
        return Result.succ("发布成功",null);
    }



    /**
     * 根据课程Id查询课程Map对象
     * @param id
     * @return
     */
    @GetMapping("vo/{id}")
    public Result getCoursePublishById(@PathVariable String id){
        Map<String, Object> map = courseService.getMapById(id);
        return Result.succ(map);
    }

    /**
     * 放到redis里  每半个小时更新一次
     * @param id
     * @return
     */
    @ApiOperation("根据课程id更改销售量")
    @GetMapping("updateBuyCount/{id}")
    public Result updateBuyCountById(
            @ApiParam(value = "课程id", required = true)
            @PathVariable String id){
        String s = UUID.randomUUID().toString();
        if(redisTemplate.opsForValue().setIfAbsent("buy"+id,s,30,TimeUnit.SECONDS)) {
            redisTemplate.opsForHash().increment("buy", id, 1);
        }else{
            return Result.succ("null");
        }
        return Result.succ(null);
    }





}
