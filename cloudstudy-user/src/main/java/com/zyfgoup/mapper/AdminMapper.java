package com.zyfgoup.mapper;

import com.zyfgoup.entity.Admin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 管理端登录用户 Mapper 接口
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-20
 */
public interface AdminMapper extends BaseMapper<Admin> {
    /**
     * 根据用户id获取角色名
     * @param id
     */
    List<String> getRoles4AdminId(String id);

}
