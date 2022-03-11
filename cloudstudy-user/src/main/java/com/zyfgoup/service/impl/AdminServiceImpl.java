package com.zyfgoup.service.impl;

import com.zyfgoup.entity.Admin;
import com.zyfgoup.mapper.AdminMapper;
import com.zyfgoup.service.AdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 管理端登录用户 服务实现类
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-20
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
    @Override
    public List<String> getRoles4AdminId(String id) {
        return baseMapper.getRoles4AdminId(id);
    }
}
