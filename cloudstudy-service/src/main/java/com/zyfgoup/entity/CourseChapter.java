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
 * 课程章节
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CourseChapter implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 章节ID
     */
    @TableId(value = "id",type = IdType.ID_WORKER_STR)
    private String id;

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 章节名称
     */
    private String title;

    /**
     * 显示排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
