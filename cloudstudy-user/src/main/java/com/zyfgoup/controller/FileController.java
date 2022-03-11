package com.zyfgoup.controller;
import com.zyfgoup.entity.Result;
import com.zyfgoup.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author Zyfgoup
 * @Date 2021/3/12 11:20
 * @Description
 */
@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    FileService fileService;

    @DeleteMapping("remove")
    public Result remove(@RequestBody String url){
        fileService.removeFile(url);
        return Result.succ("删除成功");
    }

    @PostMapping("/upload")
    public Result uploadOssFile(MultipartFile file) {
        //获取上传文件  MultipartFile
        //返回上传到oss的路径
        String url =fileService.upload(file);
        return Result.succ(url);
    }
}
