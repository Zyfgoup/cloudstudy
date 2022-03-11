package com.zyfgoup.service;

import com.zyfgoup.entity.Admin;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 管理端登录用户 服务类
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-20
 */
public interface AdminService extends IService<Admin> {
    List<String> getRoles4AdminId(String id);
}
