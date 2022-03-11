package com.zyfgoup.jwt;

import com.alibaba.fastjson.JSON;
import com.zyfgoup.entity.Result;
import com.zyfgoup.entity.UserVO;
import com.zyfgoup.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author Zyfgoup
 * @Date 2020/12/31 17:06
 * @Description 处理管理端退出
 */
@Slf4j
public class MyLogoutHandler implements LogoutSuccessHandler {

    private StringRedisTemplate redisTemplate;



    public MyLogoutHandler(RedisTemplate redisTemplate){
        this.redisTemplate = (StringRedisTemplate) redisTemplate;
    }

    /**
     * 处理退出
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("登出");

        String token = request.getHeader("Authorization");
        log.info("token:"+token);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");


        Claims claimByToken = JwtUtils.getClaimByToken(token);
        //根据实体来生成的token
        try {
            String user = claimByToken.getSubject();
            UserVO userVO = JSON.parseObject(user, UserVO.class);
            String userid = String.valueOf(userVO.getId());
            if ("".equals(userid) || userid == null) {
                response.getWriter().write(JSON.toJSONString(Result.fail("退出失败,用户ID为空")));

            }
            //删除redis里的token和权限
            redisTemplate.delete("JWT" + userid + ":");
            redisTemplate.delete("JWT" + userid + ":" + ":Authorities");
        }finally {
            //不管怎样都登出成功
            response.getWriter().write(JSON.toJSONString(Result.succ(200,"退出成功",null)));
            log.info("登出成功");
        }


    }
}
