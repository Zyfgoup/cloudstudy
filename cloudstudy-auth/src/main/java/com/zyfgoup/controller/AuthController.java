package com.zyfgoup.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.zyfgoup.entity.Result;
import com.zyfgoup.entity.UserVO;
import com.zyfgoup.client.UserService2;
import com.zyfgoup.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author Zyfgoup
 * @Date 2020/12/30 22:11
 * @Description
 */
@RestController
@Slf4j
public class AuthController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserService2 userService2;

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 注册
     * @param map
     * @return
     */
    @PostMapping("/register")
    public Result register(@RequestBody Map<String,Object> map){
       return userService2.add(map);
    }

    /**
     * 网站用户登录
     * @param map
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody Map<String,Object> map, HttpServletResponse response){
       Result result =  userService2.login(map);

       if(result.getCode() == 200) {
           //生成token
           UserVO userVO = BeanUtil.copyProperties(result.getData(), UserVO.class);
           userVO.setAdmin(false);
           String jwtToken = JwtUtils.generateToken(JSON.toJSONString(userVO));
           //application/json
           response.setContentType(MediaType.APPLICATION_JSON_VALUE);
           response.setCharacterEncoding("UTF-8");
           response.setHeader("Authorization", jwtToken);
           //将Authorization在响应首部暴露出来
           response.setHeader("Access-control-Expose-Headers", "Authorization");
           //token的键 存放在redis中
//           String key = "JWT" + userVO.getId() + ":";
//           //JwtUtils.getExpire()  配置文件配置的过期时间  使用config配置中心 可以动态改
//           redisTemplate.opsForValue().set(key, jwtToken, JwtUtils.getExpire(), TimeUnit.SECONDS);

           //返回vo
           result.setData(JSON.toJSONString(userVO));
       }
       return result;
    }

    /**
     * 一开始使用Security的登出 但是不知道为什么会走认证 一直认证失败 不晓得原因
     * @param request
     * @return
     */
    @PostMapping("/admin/logout")
    public Result adminLogout(HttpServletRequest request) {
        log.info("登出");

        String token = request.getHeader("Authorization");
        log.info("token:" + token);

        Claims claimByToken = JwtUtils.getClaimByToken(token);
        //根据实体来生成的token
        try {
            String user = claimByToken.getSubject();
            UserVO userVO = JSON.parseObject(user, UserVO.class);
            String userid = String.valueOf(userVO.getId());
            //删除redis里的token和权限
            redisTemplate.delete("JWT" + userid + ":");
            redisTemplate.delete("JWT" + userid + ":" + ":Authorities");
        } finally {
            log.info("登出成功");
           return Result.succ(200, "退出成功", null);

        }
    }


}
