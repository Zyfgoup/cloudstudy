package com.zyfgoup.entity.dto;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Zyfgoup
 * @Date 2021/2/2 19:01
 * @Description  分页查询时 返回该类实例
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageDTO {
    private Object data;
    private Long current;
    private Long size;
    private Long total;


    /**
     * 将page里的参数 构建PageDTO对象
     * @param page
     * @return
     */
    public static PageDTO get(IPage page){
        return PageDTO.builder().data(page.getRecords())
                .current(page.getCurrent())
                .size(page.getSize())
                .total(page.getTotal()).build();
    }
}
