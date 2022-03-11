package com.zyfgoup.controller;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyfgoup.client.UserClient;
import com.zyfgoup.entity.EduComment;
import com.zyfgoup.entity.Result;
import com.zyfgoup.entity.UserVO;
import com.zyfgoup.exception.BaseException;
import com.zyfgoup.exception.ErrorCode;
import com.zyfgoup.service.EduCommentService;
import com.zyfgoup.utils.JwtUtils;
import com.zyfgoup.words.WordFilter;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 评论 前端控制器
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-16
 */
@RestController
@RequestMapping("/comment")
public class EduCommentController {
    @Autowired
    private EduCommentService commentService;

    @Autowired
    private UserClient userClient;

    //根据课程id查询评论列表
    @ApiOperation(value = "评论分页列表")
    @GetMapping("commentList/{page}/{limit}")
    public Result index(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,

            @ApiParam(name = "courseQuery", value = "查询对象", required = false)
                    String courseId) {
        Page<EduComment> pageParam = new Page<>(page, limit);
        QueryWrapper<EduComment> wrapper = new QueryWrapper<>();

        if(!StringUtils.isEmpty(courseId)){
            //构建条件
            wrapper.eq("course_id",courseId);
        }

        commentService.page(pageParam,wrapper);
        List<EduComment> commentList = pageParam.getRecords();

        Map<String, Object> map = new HashMap<>();
        map.put("items", commentList);
        map.put("current", pageParam.getCurrent());
        map.put("pages", pageParam.getPages());
        map.put("size", pageParam.getSize());
        map.put("total", pageParam.getTotal());
        map.put("hasNext", pageParam.hasNext());
        map.put("hasPrevious", pageParam.hasPrevious());
        return Result.succ(map);
    }

    @ApiOperation(value = "添加评论")
    @PostMapping("saveComment")
    public Result save(@RequestBody EduComment comment, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if(StrUtil.isBlank(token)){
            throw new BaseException(ErrorCode.TOKEN_EMPTY);
        }
        UserVO userVo = JwtUtils.getUserVo(token);

        if(ObjectUtil.isNull(userVo)){
            throw new BaseException(ErrorCode.TOKEN_ERROR);
        }
        comment.setMemberId(userVo.getId());

        Result result = userClient.outGetOne(userVo.getId());
        if(result.getCode() == 200){
            Map<String, Object> map = (Map<String, Object>) result.getData();
            comment.setNickname((String)map.get("nickname"));
            comment.setAvatar((String)map.get("avatar"));

            //过滤敏感词汇
            comment.setContent(WordFilter.replaceWords(comment.getContent()));
            commentService.save(comment);
            return Result.succ("评论成功");
        }
        return Result.fail("评论失败");


    }

    @ApiOperation(value = "删除评论")
    @DeleteMapping("deleteComment/{commentId}")
    public Result delete(@PathVariable String commentId, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if(StrUtil.isBlank(token)){
            throw new BaseException(ErrorCode.TOKEN_EMPTY);
        }
        UserVO userVo = JwtUtils.getUserVo(token);

        if(ObjectUtil.isNull(userVo)){
            throw new BaseException(ErrorCode.TOKEN_ERROR);
        }
        boolean result = commentService.remove(new QueryWrapper<EduComment>().eq("id",commentId)
                .eq("member_id",userVo.getId()));
        if(result){
            return Result.succ("删除成功",null);
        }else{
            return Result.fail("数据不存在");
        }
    }

    //判断是否是该用户的评论
    @ApiOperation(value = "判断是否是该用户的评论")
    @GetMapping("isComment/{commentId}")
    public Result isComment(
            @ApiParam(name = "commentId", value = "评论id", required = true)
            @PathVariable String commentId,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization");
        if(StrUtil.isBlank(token)){
            throw new BaseException(ErrorCode.TOKEN_EMPTY);
        }
        UserVO userVo = JwtUtils.getUserVo(token);

        if(ObjectUtil.isNull(userVo)){
            throw new BaseException(ErrorCode.TOKEN_ERROR);
        }
        QueryWrapper<EduComment> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("id", commentId)
                .eq("member_id",userVo.getId());
        int count = commentService.count(queryWrapper);
        if(count>0)
           return Result.succ(true);
        else
            return Result.fail("不属于该用户",false);
    }

}
