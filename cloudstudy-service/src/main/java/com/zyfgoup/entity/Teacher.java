package com.zyfgoup.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.internal.util.BackpressureDrainManager;

/**
 * <p>
 * 讲师
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Teacher implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 讲师ID
     */
    @TableId(value = "id",type = IdType.ID_WORKER_STR)
    private String id;

    /**
     * 讲师姓名
     */
    private String name;

    /**
     * 讲师资历,一句话说明讲师
     */
    private String intro;

    /**
     * 讲师简介
     */
    private String career;

    /**
     * 头衔 1高级讲师 2首席讲师 3普通讲师
     */
    private Integer level;

    /**
     * 讲师头像
     */
    private String avatar;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 逻辑删除 1（true）已删除， 0（false）未删除
     */
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
