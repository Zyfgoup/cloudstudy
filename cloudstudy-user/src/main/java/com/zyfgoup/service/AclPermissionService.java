package com.zyfgoup.service;

import com.zyfgoup.entity.AclPermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 权限 服务类
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-04
 */
public interface AclPermissionService extends IService<AclPermission> {

    List<String> selectPermissionValueByUserId(String id);

    List<String> selectAllPermissionValue();

    List<AclPermission> selectPermissionByUserId(String userId);

}
