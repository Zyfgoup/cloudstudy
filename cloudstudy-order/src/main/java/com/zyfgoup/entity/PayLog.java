package com.zyfgoup.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 支付日志表
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PayLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.ID_WORKER_STR)
    private String id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付完成时间
     */
    private LocalDateTime payTime;

    /**
     * 支付金额（分）
     */
    private BigDecimal totalFee;

    /**
     * 交易流水号
     */
    private String transactionId;

    /**
     * 交易状态
     */
    private String tradeState;

    /**
     * 支付类型（1：微信 2：支付宝）
     */
    private Integer payType;

    /**
     * 其他属性
     */
    private String attr;

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
