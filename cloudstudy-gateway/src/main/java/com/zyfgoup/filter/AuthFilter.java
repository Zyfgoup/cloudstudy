package com.zyfgoup.filter;

import com.alibaba.fastjson.JSON;
import com.zyfgoup.entity.Authority;
import com.zyfgoup.entity.Result;
import com.zyfgoup.entity.UserVO;
import com.zyfgoup.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author Zyfgoup
 * @Date 2020/12/30 19:27
 * @Description
 *
 */
@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {
    AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 白名单  登录注册注销   使用Vue-simple-upload的简单上传合并也放行
     * swagger2的也要放行 无论访问/swagger2-ui.html  还是/doc.html
     *  都是去访问/api/*\/v2api-docs/**
     *  /api/admin/auth 管理端登录退出
     *  /api/admin/user/permission 是获取权限相关
     */
    private static final String[] EXCLUSIONURLS = {"/api/auth/login","/api/auth/register","/api/auth/logout"
    ,"/api/*/v2/api-docs/**","/api/admin/auth/admin/login","/api/admin/auth/admin/logout"
    ,"/api/admin/user/permission/get/buttons/**","/api/admin/user/permission/menu/**",
    "/api/admin/service/chart/**","/api/client/**"};


    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().getPath();
        log.info("request path:{}", path);

        //1、判断是否是白名单的路径， 是的话就放行
        boolean isWhite = Arrays.stream(EXCLUSIONURLS).anyMatch(exclusionurl ->
                antPathMatcher.match(exclusionurl, path));
        if (isWhite){
            return chain.filter(exchange);
        }

        //2 没有带上token 或者Token无效
        String headerToken = request.getHeaders().getFirst("Authorization");
        log.info("headerToken:{}", headerToken);
        if ( StringUtils.isEmpty(headerToken) || !verifierToken(headerToken)){
            log.error("token为空或者token无效");
            return getVoidMono(response, 401, "请重新登录");
        }


        //根据token生成的对象是不是管理端用户
        UserVO userVO = getUserVO(headerToken);
        if(userVO.isAdmin() || antPathMatcher.match("/api/admin/*/**", path)) {
            // 如果是/api/admin/*/** 表示管理端请求 需要鉴权 这里鉴权只是简单判断角色而已

            //判断请求的URL是否有权限
            boolean permission = hasPermission(userVO.getId(), path);
            if (!permission) {
                //gateway不能使用web依赖
                return getVoidMono(response, 401, "无访问权限");
            }
            return chain.filter(exchange);
        }

        //不是管理端请求 不需要验证权限
        return chain.filter(exchange);

    }

    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 验证token
     * @param headerToken
     * @return
     */
    private boolean verifierToken(String headerToken){
            Claims claim = JwtUtils.getClaimByToken(headerToken);
            if(claim==null){
                log.error("token过期或者无效");
                return false;
            }
            String jsonUser = claim.getSubject();
            String userid = JSON.parseObject(jsonUser,UserVO.class).getId();
            if (StringUtils.isEmpty(userid)){
                return false;
            }

            //去redis找是否有  校验是否有效
            String redisToken = redisTemplate.opsForValue().get("JWT"+userid+":");
            if ("".equals(redisToken)||!redisToken.equals(headerToken)) {
                log.error("token不合法，检测不过关");
                return false;
            }
            //校验超时
            if(JwtUtils.isTokenExpired(claim.getExpiration())) {
                // token过期了
                log.error("token已经过期");
                return false;
            }
            return true;

    }

    /**
     * 暂时只有superadmin 可以通过
     * @param userid
     * @param path
     * @return
     */
    private boolean hasPermission(String userid,String path){

            //构建Key， 把权限放入到redis中
            String key = "JWT" + userid+ ":";
            String authKey = key + ":Authorities";

            //权限
            String authStr = redisTemplate.opsForValue().get(authKey);
            if (StringUtils.isEmpty(authStr)){
                log.info("权限为空 鉴权失败");
                return false;
            }

            //匹配角色
        List<Authority> authorities = JSON.parseArray(authStr , Authority.class);
//            AtomicBoolean flag = new AtomicBoolean(false);
//            authorities.forEach(authority -> {
//                if("ROLE_超级管理员".equals(authority.getAuthority())){
//                    flag.set(true);
//                }
//            });
//
//            return flag.get();


            //这是匹配请求路径的   客户端请求都是 /api/admin/服务名/具体请求
            //去掉前1个
            String[] str = path.split("/");
            StringBuilder newPath = new StringBuilder("/");
            //去掉/api/admin/服务名/
            for (int i = 4; i <str.length-1 ; i++) {
                newPath.append(str[i]+"/");

            }
            newPath.append(str[str.length-1]);
            return authorities.stream().anyMatch(authority -> antPathMatcher.match(authority.getAuthority(), newPath.toString()));

    }

    private Mono<Void> getVoidMono(ServerHttpResponse response, int i, String msg) {
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        //401错误
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        Result failed = Result.fail(i, msg,null);
        byte[] bits = JSON.toJSONString(failed).getBytes();
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        return response.writeWith(Mono.just(buffer));
    }


    /**
     * 获取token里的对象
     * @param headerToken
     * @return
     */
    private UserVO getUserVO(String headerToken){
       return JSON.parseObject(JwtUtils.getClaimByToken(headerToken).getSubject(),UserVO.class);
    }

}
