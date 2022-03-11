package com.zyfgoup.controller;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.zyfgoup.entity.CourseCollect;
import com.zyfgoup.entity.Result;
import com.zyfgoup.entity.UserVO;
import com.zyfgoup.entity.vo.CourseCollectVo;
import com.zyfgoup.exception.BaseException;
import com.zyfgoup.exception.ErrorCode;
import com.zyfgoup.service.CourseCollectService;
import com.zyfgoup.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 课程收藏 前端控制器
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-15
 */
@RestController
@RequestMapping("/collect")
public class CourseCollectController {

    @Autowired
    private CourseCollectService courseCollectService;

    //1.收藏课程  添加一条收藏的记录
    @ApiOperation(value = "添加收藏课程")
    @PostMapping("addCourseCollect/{courseId}")
    public Result addCourseCollect(@ApiParam(name = "courseId", value = "课程id", required = true)
                              @PathVariable String courseId, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if(StrUtil.isBlank(token)){
            throw new BaseException(ErrorCode.TOKEN_EMPTY);
        }
        UserVO userVo = JwtUtils.getUserVo(token);

        if(ObjectUtil.isNull(userVo)){
            throw new BaseException(ErrorCode.TOKEN_ERROR);
        }

        QueryWrapper<CourseCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("course_id", courseId)
                .eq("user_id", userVo.getId());

        CourseCollect one = courseCollectService.getOne(queryWrapper);
        //未收藏则收藏
        if(ObjectUtil.isNull(one)) {
           CourseCollect courseCollect = new CourseCollect();
            courseCollect.setCourseId(courseId);
            courseCollect.setUserId(userVo.getId());
            courseCollectService.save(courseCollect);
            return Result.succ(null);
        }else{
            return Result.fail("您已收藏,请勿重复操作");
        }

    }

    //2.获取课程收藏列表
    @ApiOperation(value = "获取课程收藏列表")
    @GetMapping("courseCollectList")
    public Result showCourseCollect(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if(StrUtil.isBlank(token)){
            throw new BaseException(ErrorCode.TOKEN_EMPTY);
        }
        UserVO userVo = JwtUtils.getUserVo(token);

        if(ObjectUtil.isNull(userVo)){
            throw new BaseException(ErrorCode.TOKEN_ERROR);
        }
        List<CourseCollectVo> list = courseCollectService.selectByMemberId(userVo.getId());
        return Result.succ(list);
    }

    //3.取消收藏  删除一条收藏的记录
    @ApiOperation(value = "取消收藏课程")
    @DeleteMapping("remove/{courseId}")
    public Result removeCourseCollect(
            @ApiParam(name = "courseId", value = "课程id", required = true)
            @PathVariable String courseId,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization");
        if(StrUtil.isBlank(token)){
            throw new BaseException(ErrorCode.TOKEN_EMPTY);
        }
        UserVO userVo = JwtUtils.getUserVo(token);

        if(ObjectUtil.isNull(userVo)){
            throw new BaseException(ErrorCode.TOKEN_ERROR);
        }

        boolean result = courseCollectService.remove(new QueryWrapper<CourseCollect>().eq("course_id",courseId)
        .eq("user_id",userVo.getId()));
        if (result) {
            return Result.succ("已取消收藏",null);
        } else {
            return Result.fail("取消失败");
        }
    }

    //4.判断是否收藏
    @ApiOperation(value = "判断是否收藏")
    @GetMapping("is-collect/{courseId}")
    public Result isCollect(
            @ApiParam(name = "courseId", value = "课程id", required = true)
            @PathVariable String courseId,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization");
        if(StrUtil.isBlank(token)){
            throw new BaseException(ErrorCode.TOKEN_EMPTY);
        }
        UserVO userVo = JwtUtils.getUserVo(token);

        if(ObjectUtil.isNull(userVo)){
            throw new BaseException(ErrorCode.TOKEN_ERROR);
        }

        QueryWrapper<CourseCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("course_id", courseId)
                .eq("user_id", userVo.getId());
        Integer count = courseCollectService.count(queryWrapper);
        if(count>0)
            return Result.succ(true);
        else
            return Result.succ(false);
    }
}
