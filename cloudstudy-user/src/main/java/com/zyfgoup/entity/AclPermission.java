package com.zyfgoup.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 权限
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AclPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(type = IdType.ID_WORKER_STR,value = "id")
    private String id;

    /**
     * 所属上级
     */
    private String pid;

    /**
     * 名称
     */
    private String name;


    /**
     * 类型(1:菜单,2:按钮)
     */
    private Integer type;

    @ApiModelProperty(value = "层级")
    @TableField(exist = false)
    private Integer level;

    @ApiModelProperty(value = "下级")
    @TableField(exist = false)
    private List<AclPermission> children;


    @ApiModelProperty(value = "是否选中")
    @TableField(exist = false)
    private boolean isSelect;

    /**
     * 权限值
     */
    private String permissionValue;


    /**
     * 请求路径
     */
    private String url;

    /**
     * 访问路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 状态(0:禁止,1:正常)
     */
    private Integer status;

    /**
     * 逻辑删除 1（true）已删除， 0（false）未删除
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
