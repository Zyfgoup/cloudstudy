package com.zyfgoup.utils;

import com.alibaba.fastjson.JSON;
import com.zyfgoup.entity.UserVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * jwt工具类
 * @author zyfgoup
 */
@Slf4j
public class JwtUtils {

    private static final String SECRET = "f4e2e52034348f86b67cde581c0f9eb5";

    /**
     * 过期时间 3天
     */
    private static final long EXPIRE = 259200;

    /**
     * 生成jwt token
     */
    public static String generateToken(String jsonUser) {
        Date nowDate = new Date();
        //过期时间
        Date expireDate = new Date(nowDate.getTime() + EXPIRE * 1000);
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(jsonUser)
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public static  Claims getClaimByToken(String token) {
        try {
            log.info(token);
            return Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        }catch (Exception e){
            log.debug("解析token失败",e);
            return null;
        }
    }


    public static UserVO  getUserVo(String token){
        Claims claim = JwtUtils.getClaimByToken(token);
        if(claim == null){
            return null;
        }
        String jsonUser = claim.getSubject();
        if(StringUtils.isEmpty(jsonUser)){
            return null;
        }
        UserVO userVO = JSON.parseObject(jsonUser, UserVO.class);
        return userVO;
    }

    /**
     * token是否过期
     * @return  true：过期
     */
    public static  boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date());
    }

    public static long getExpire(){
        return EXPIRE;
    }
}
