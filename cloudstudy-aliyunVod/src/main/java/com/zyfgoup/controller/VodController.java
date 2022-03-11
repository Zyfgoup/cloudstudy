package com.zyfgoup.controller;

import com.aliyuncs.exceptions.ClientException;
import com.zyfgoup.entity.Result;
import com.zyfgoup.service.VodService;
import com.zyfgoup.utils.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * @Author Zyfgoup
 * @Date 2021/1/11 13:43
 * @Description
 */
@RestController
public class VodController {

    @Autowired
    private VodService vodService;

    @Autowired
    StringRedisTemplate redisTemplate;


    /**
     * 上传视频到阿里云的视频点播  只能上传视频
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("/video/upload")
    public Result uploadVideo(MultipartFile file) throws Exception {
        String videoSourceId = vodService.uploadVideo(file);
        return Result.succ("上传成功",videoSourceId);
    }

    @GetMapping("/video/playauth/{videoId}")
    public Result getVideoPlayAuth(
            @PathVariable String videoId) throws ClientException {
        try {
            String playAuth = vodService.getVideoPlayAuth(videoId);

            if (!StringUtils.isEmpty(playAuth)) {
                //如果一直点击这里要解决并发问题... 一直点击问题
                    redisTemplate.opsForValue().increment(RedisKey.VIDEO_PLAY_NUM,1);
            }
            return Result.succ(playAuth);
        }catch (Exception e){
            return Result.fail("播放失败");
        }
    }

    /**
     * 根据视频ID删除视频
     * @return
     */
    @DeleteMapping("/video/{videoSourceId}")
    public Result deleteVideoById(@PathVariable String videoSourceId){
        if(StringUtils.isEmpty(videoSourceId)){
            return Result.fail("ID不能为空");
        }
        Boolean flag = vodService.deleteVodById(videoSourceId);
        if(flag){
            return Result.succ(null);
        }
        return Result.fail("删除视频失败");
    }


    /**
     * 批量删除视频
     * @return
     */
    @DeleteMapping("/video/removeList")
    public Result removeVideoList(
            @RequestParam("videoIdList") List videoIdList){
        Boolean flag = vodService.removeVideoList(videoIdList);
        if(flag){
            return Result.succ(null);
        }
        return Result.fail("批量删除视频失败");
    }
}
