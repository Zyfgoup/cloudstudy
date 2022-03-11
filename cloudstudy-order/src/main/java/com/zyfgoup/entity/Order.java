package com.zyfgoup.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.internal.util.BackpressureDrainManager;

/**
 * <p>
 * 订单
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_order")
@Accessors(chain = true)
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.ID_WORKER_STR)
    private String id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 课程id
     */
    private String courseId;

    /**
     * 课程名称
     */
    private String courseTitle;

    /**
     * 课程封面
     */
    private String courseCover;

    /**
     * 讲师名称
     */
    private String teacherName;

    /**
     * 会员id
     */
    private String userId;

    /**
     * 会员昵称
     */
    private String nickname;

    /**
     * 会员手机
     */
    private String mobile;

    /**
     * 订单金额（分）
     */
    private BigDecimal totalFee;

    /**
     * 支付类型（1：微信 2：支付宝）
     */
    private Integer payType;

    /**
     * 订单状态（0：未支付 1：已支付）
     */
    private Integer status;

    /**
     * 逻辑删除 1（true）已删除， 0（false）未删除
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
