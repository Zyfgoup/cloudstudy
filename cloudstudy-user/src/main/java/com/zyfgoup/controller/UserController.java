package com.zyfgoup.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.deploy.association.RegisterFailedException;
import com.zyfgoup.dto.LoginDTO;
import com.zyfgoup.entity.Result;
import com.zyfgoup.entity.User;
import com.zyfgoup.entity.UserVO;
import com.zyfgoup.exceImpl.RegisteException;
import com.zyfgoup.exceImpl.UploadAvatarException;
import com.zyfgoup.exception.BaseException;
import com.zyfgoup.exception.ErrorCode;
import com.zyfgoup.service.FileService;
import com.zyfgoup.service.UserService;
import com.zyfgoup.utils.RedisKey;
import com.zyfgoup.vo.UserVo;
import com.zyfgoup.words.WordFilter;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * <p>
 * 网站用户表 前端控制器
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-20
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    private final static String[] FILETYPE = {".jpg",".jpeg",".png"};


    @Autowired
    UserService userService;

    @Autowired
    FileService fileService;

    @Autowired
    StringRedisTemplate redisTemplate;


    //查询某天注册人数
    @GetMapping("countRegister/{day}")
    public Result countRegister(@PathVariable String day){
        Integer count = userService.countRegister(day);
        return Result.succ(count);
    }

    //根据名字获取信息
    @PostMapping("/getInfoByName/{nickname}")
    public Result getInfoByName(@PathVariable("nickname") String nickname){
        User nickname1 = userService.getOne(new QueryWrapper<User>().eq("nickname", nickname));
        return Result.succ(nickname1);
    }


    /**
     * 创建用户 注册
     * @param map
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody Map<String,Object> map){
        User user = BeanUtil.mapToBean(map, User.class, false);

        if(StrUtil.isBlank(user.getMobile()) || StrUtil.isBlank(user.getNickname()) || StrUtil.isBlank(user.getPassword())){
            throw new RegisteException();
        }

        //根据手机号查询 用户名
        User user1 = userService.getOne(new QueryWrapper<User>().eq("mobile",user.getMobile()));
        if(!ObjectUtil.isNull(user1)) {
            return Result.fail("手机号已创建用户");
        }
        if(!ObjectUtil.isNull(userService.getOne(new QueryWrapper<User>().eq("nickname",user.getNickname())))){
            return Result.fail("该昵称已存在");
        }

        //加密
        user.setPassword(SecureUtil.md5(user.getPassword()));

        //敏感词处理
        if(WordFilter.haveSensitiveWord(user.getNickname())){
            return Result.fail("昵称不能含有敏感词");
        }

        //默认头像
        user.setAvatar("https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png");

        if(userService.save(user)) {
            return Result.succ("创建成功", null);
        }
        else {
            return Result.fail("创建用户失败");
        }
    }

    /**
     * 删除用户 逻辑删除
     */
    @DeleteMapping("/delete/{userid}")
    public Result delete(@PathVariable("userid") String userid){
        boolean b = userService.update(new UpdateWrapper<User>().set("is_deleted", 1).eq("id", userid));
        if(b){
            return Result.succ("删除成功",null);
        }
        return Result.fail("删除失败");
    }

    /**
     * 获取单个用户信息
     * @param userid
     * @return
     */
    @GetMapping("/get/{userid}")
    public Result getOne(@PathVariable("userid") String userid){
        User byId = userService.getById(userid);
        if(ObjectUtil.isNotNull(byId)){
            //把密码设置为null
            byId.setPassword(null);

            return Result.succ(byId);
        }
        return Result.fail("获取用户信息失败");
    }

    @GetMapping("/out/get/{userid}")
    public Result outGetOne(@PathVariable("userid") String userid){
        User byId = userService.getById(userid);
        if(ObjectUtil.isNotNull(byId)){
            //把密码设置为null
            byId.setPassword(null);

            return Result.succ(BeanUtil.beanToMap(byId));
        }
        return Result.fail("获取用户信息失败");
    }


    /**
     * 修改
     * @param user
     * @return
     */
    @PutMapping("/modify")
    public Result modify(@RequestBody User user){
        boolean b = userService.updateById(user);
        User byId = userService.getById(user.getId());
        if(b){
            //返回最新的用户信息
            return Result.succ("修改成功",byId);
        }else{
            return Result.fail("修改失败");
        }
    }

    /**
     * 根据条件查询用户
     * @param params
     * @param current
     * @param size
     * @return
     */
    @GetMapping("/get/{params}/{current}/{size}")
    public Result get(@PathVariable("params") String params,Long current,Long size){
       IPage page = new Page(current,size);
        JSONObject jsonObject = JSONObject.parseObject(params);
        String mobile = (String) jsonObject.get("mobile");
       String nickname = (String) jsonObject.get("nickname");

       //动态sql查询
        userService.page(page,
                new QueryWrapper<User>().eq(!StrUtil.isBlank(mobile), "mobile", mobile)
                        .eq(!StrUtil.isBlank(nickname), "nickname", nickname));

        UserVo userVo = new UserVo(current,size,page.getTotal(),page.getRecords());
        return Result.succ(userVo);
    }

    @PostMapping("/list")
    public Result getList() {

        if(redisTemplate.hasKey(RedisKey.ALL_USER)){
            return Result.succ(JSON.parseArray(redisTemplate.opsForValue().get(RedisKey.ALL_USER),User.class));
        }
        List<User> list = userService.list();
        redisTemplate.opsForValue().set(RedisKey.ALL_USER,JSON.toJSONString(list),2, TimeUnit.HOURS);
        return Result.succ(list);
    }


    /**
     * 登录
     * @param map
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody Map<String,Object> map){
        LoginDTO loginDTO = BeanUtil.mapToBean(map, LoginDTO.class, false);
        User user = userService.getOne(new QueryWrapper<User>().eq("mobile",loginDTO.getMobile()));

        if(ObjectUtil.isNull(user)){
            return Result.fail("手机号不存在,请先注册");
        }
        String encodePwd = SecureUtil.md5(loginDTO.getPassword());
        log.info(encodePwd);
        if(!encodePwd.equals(user.getPassword())) {
            return Result.fail("密码错误");

        }
        redisTemplate.opsForSet().add(RedisKey.LOGIN_NUM,user.getId());
        return Result.succ("登录成功",user);
    }

    /**
     * oss 图片上传
     * @param file
     * @return
     * @throws UploadAvatarException
     */
    @PostMapping("/avatar/upload")
    public Result upload(MultipartFile file) throws UploadAvatarException {
       if(ObjectUtil.isNull(file)){
           return Result.fail("未选择图片");
       }
        String fileName = file.getOriginalFilename();
        String filetype = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

        //判断图片格式
        boolean flag = false;
        for (String s : FILETYPE) {
            if(s.equals(filetype))
                flag =true;
        }
        if(!flag){
            return Result.fail("图片格式不正确");
        }

        String uploadUrl = fileService.upload(file);
        return Result.succ("上传成功",uploadUrl);
    }



    /**
     * 修改密码 已经登录后修改
     * 需要带上手机号、旧密码  新密码 id
     * 如果是修改 得判断id是否存在 不存在 重新登录
     * 或者是绑定新手机与设置新密码
     */

    @PostMapping("/modifypwd")
    public Result modifyPwd(@RequestBody Map<String, String> map){

        //绑定手机号
        if(!Boolean.valueOf(map.get("isUpdate")) ){
            User one = userService.getOne(new QueryWrapper<User>().eq("id", map.get("id")));
            if(one == null){
                throw new BaseException(ErrorCode.RELOGIN);
            }
            User one1 = userService.getOne(new QueryWrapper<User>().eq("mobile", map.get("mobie")));
            if(one1 != null){
                return Result.fail("该手机号已被绑定");
            }

            one.setMobile(map.get("mobile"));
            one.setPassword( SecureUtil.md5(map.get("password")));
            userService.updateById(one);
            return Result.succ(null);


        }


        User user = userService.getOne(new QueryWrapper<User>().eq("mobile", map.get("mobile"))
        .eq("id",map.get("id")));

        if(ObjectUtil.isNull(user)){
            return Result.fail("手机号与用户所绑定不一致");
        }

        String encodePwd = SecureUtil.md5(map.get("oldPassword"));
        if(!user.getPassword().equals(encodePwd)){
            return Result.fail("密码不正确");
        }

        user.setPassword(SecureUtil.md5(map.get("password")));

        boolean b = userService.updateById(user);
        if(!b){
            return Result.fail("修改密码失败");
        }

        return Result.succ("修改成功",null);

    }


    /**
     * 忘记密码
     * @param map
     * @return
     */
    @PostMapping("/resetpwd")
    public Result resetPwd(@RequestBody Map<String, String> map){
        User user = userService.getOne(new QueryWrapper<User>().eq("mobile", map.get("mobile")));

        if(ObjectUtil.isNull(user)){
            return Result.fail("用户不存在");
        }

        user.setPassword(SecureUtil.md5(map.get("password")));

        boolean b = userService.updateById(user);
        if(!b){
            return Result.fail("重置密码失败");
        }

        return Result.succ("重置成功",null);
    }


    /**
     * 管理端 禁用某个用户
     * @param id
     * @return
     */
    @PostMapping("/disabled")
    public Result disadbled(String id){
        boolean b = userService.update(new UpdateWrapper<User>().eq("id", id).set("is_disabled", 1));
        if(b){
            return Result.succ("禁用成功",null);
        }
        return Result.fail("禁用失败");
    }
    
}
