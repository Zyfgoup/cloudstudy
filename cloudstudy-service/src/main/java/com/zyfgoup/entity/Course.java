package com.zyfgoup.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 课程
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 课程ID
     */
    @TableId(type = IdType.ID_WORKER_STR,value = "id")
    private String id;

    /**
     * 课程讲师ID
     */
    private String teacherId;

    /**
     * 一级分类ID
     */
    private String categoryParentId;

    /**
     * 具体类别ID
     */
    private String categoryId;


    /**
     * 课程简介
     */
    private String description;

    /**
     * 课程标题
     */
    private String title;

    /**
     * 课程销售价格，设置为0则可免费观看
     */
    private BigDecimal price;

    /**
     * 总课时
     */
    private Integer lessonNum;

    /**
     * 课程封面图片路径
     */
    private String cover;

    /**
     * 销售数量
     */
    private Long buyCount;

    /**
     * 浏览数量
     */
    private Long viewCount;

    /**
     * 点赞数量
     */
    private Long likeCount;

    /**
     * 乐观锁
     */
    private Long version;

    /**
     * 视频状态 Draft未发布  Normal已发布
     */
    private String status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;


}
