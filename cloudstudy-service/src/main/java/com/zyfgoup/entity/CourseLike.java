package com.zyfgoup.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 课程点赞表
 * </p>
 *
 * @author zyfgoup
 * @since 2021-02-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseLike implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 点赞ID
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 被点赞课程ID
     */
    private String courseId;

    /**
     * 点赞用户id
     */
    private String userId;

    /**
     * 点赞状态，0未点赞，1点赞
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;


}
