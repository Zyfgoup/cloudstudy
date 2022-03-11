package com.zyfgoup.entity;

import java.math.BigDecimal;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.ID_WORKER_STR)
    private String id;

    private String courseId;

    private String userId;

    private String teacherName;

    private String title;

    private BigDecimal price;

    private String teacherId;
    /**
     * 课程封面
     */
    private String cover;


}
