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
 * 课程视频 但实际是在章节下面的
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CourseVideo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 视频ID
     */
    @TableId(type = IdType.ID_WORKER_STR,value = "id")
    private String id;

    /**
     * 课程ID
     */
    private String courseId;

    /**
     * 章节ID
     */
    private String chapterId;

    /**
     * 视频名称
     */
    private String title;

    /**
     * 排序字段 一个章节下面可以有多个视频
     */
    private Integer sort;

    /**
     * 播放次数
     */
    private Long playCount;

    /**
     * 是否可以试听：0免费 1收费
     */
    private Boolean isFree;

    /**
     * 视频资源
     */
    private String videoSourceId;

    /**
     * 视频原始文件名称
     */
    private String videoOriginalName;

    /**
     * 视频时长（秒）
     */
    private Float duration;

    /**
     * 视频状态:见阿里云文档
     */
    private String status;

    /**
     * 视频源文件大小（字节）
     */
    private Long size;

    /**
     * 乐观锁
     */
    private Long version;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
