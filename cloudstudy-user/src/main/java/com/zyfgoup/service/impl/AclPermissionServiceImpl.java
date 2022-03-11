package com.zyfgoup.service.impl;

import com.zyfgoup.entity.AclPermission;
import com.zyfgoup.mapper.AclPermissionMapper;
import com.zyfgoup.service.AclPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 权限 服务实现类
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-04
 */
@Service
public class AclPermissionServiceImpl extends ServiceImpl<AclPermissionMapper, AclPermission> implements AclPermissionService {

    @Override
    public List<String> selectPermissionValueByUserId(String id) {
        return baseMapper.selectPermissionValueByUserId(id);
    }

    @Override
    public List<String> selectAllPermissionValue() {
        return baseMapper.selectAllPermissionValue();
    }

    @Override
    public List<AclPermission> selectPermissionByUserId(String userId) {
        return baseMapper.selectPermissionByUserId(userId);
    }
}
