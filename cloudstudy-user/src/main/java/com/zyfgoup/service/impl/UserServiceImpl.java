package com.zyfgoup.service.impl;

import com.zyfgoup.entity.User;
import com.zyfgoup.mapper.UserMapper;
import com.zyfgoup.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 网站用户表 服务实现类
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-20
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public Integer countRegister(String day) {
        return baseMapper.countRegister(day);
    }
}
