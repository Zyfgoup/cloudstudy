package com.zyfgoup.mapper;

import com.zyfgoup.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 网站用户表 Mapper 接口
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-20
 */
public interface UserMapper extends BaseMapper<User> {
    //查询某天注册人数
    Integer countRegister(String day);

}
