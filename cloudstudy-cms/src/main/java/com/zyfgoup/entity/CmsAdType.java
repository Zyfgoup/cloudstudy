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
 * 推荐位
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CmsAdType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id",type = IdType.ID_WORKER_STR)
    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 更新时间
     */
    private LocalDateTime gmtModified;


}
