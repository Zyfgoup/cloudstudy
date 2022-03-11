package com.zyfgoup.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zyfgoup.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 网站用户表 服务类
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-20
 */
public interface UserService extends IService<User> {

    Integer countRegister(String day);
}
