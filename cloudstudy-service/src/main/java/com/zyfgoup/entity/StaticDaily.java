package com.zyfgoup.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 网站统计日数据
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StaticDaily implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id",type = IdType.ID_WORKER_STR)
    private String id;

    /**
     * 统计日期
     */
    private String dateCalculated;

    /**
     * 注册人数
     */
    private Integer registerNum;

    /**
     * 登录人数
     */
    private Integer loginNum;

    /**
     * 每日播放视频数
     */
    private Integer videoViewNum;

    /**
     * 每日新增课程数
     */
    private Integer courseNum;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
