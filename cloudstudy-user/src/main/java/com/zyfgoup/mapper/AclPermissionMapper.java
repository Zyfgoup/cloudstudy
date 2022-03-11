package com.zyfgoup.mapper;

import com.zyfgoup.entity.AclPermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 权限 Mapper 接口
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-04
 */
public interface AclPermissionMapper extends BaseMapper<AclPermission> {

    List<String> selectPermissionValueByUserId(String id);

    List<String> selectAllPermissionValue();

    List<AclPermission> selectPermissionByUserId(String userId);

}
