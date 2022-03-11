package com.zyfgoup.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.zyfgoup.entity.AuthUser;
import com.zyfgoup.client.UserService2;
import com.zyfgoup.entity.Result;
import com.zyfgoup.exception.DisabledException;
import com.zyfgoup.exception.NotRoleException;
import com.zyfgoup.exception.UserServiceException;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.acl.Acl;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Zyfgoup
 * @Date 2020/12/30 18:17
 * @Description
 */
@Service
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserService2 userService2;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Map<String, Object> res = userService2.getByName(username);
        Map<String,Object> mapAdmin = (Map<String, Object>) res.get("admin");
        if(ObjectUtil.isNull(mapAdmin)){
            throw new UsernameNotFoundException("用户名不存在");
        }

        if((Integer)mapAdmin.get("isDisabled")==1){
            throw new DisabledException();
        }
       List<String> roles = (ArrayList<String>)res.get("roles");
        //没有角色
        if(roles.size() == 0){
            throw new NotRoleException();
        }


        //获取权限
        Result result = userService2.getPermissionUrl((String) mapAdmin.get("name"));

        Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();


        //但其实只能二选一 要么是角色要么是资源uri  在gateway里进行匹配的时候 颗粒度小就是匹配uri
        //toString会带有[]  截取掉
//        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_"+roles.toString().substring(1,roles.toString().length()-1));
//        grantedAuthorities.add(grantedAuthority);
        if(result.getCode() == 200){
            List<String> urls = (List<String>) result.getData();
            for (String url : urls) {
                SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(url);
                grantedAuthorities.add(grantedAuthority);
            }
        }

        //构建认证对象
        AuthUser authUser = new AuthUser((String)mapAdmin.get("id"),(String)mapAdmin.get("name"),(String)mapAdmin.get("password"),grantedAuthorities,roles,(String)mapAdmin.get("avatar"));
        return authUser;

    }
}
