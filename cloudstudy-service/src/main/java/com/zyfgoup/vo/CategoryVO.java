package com.zyfgoup.vo;

import com.zyfgoup.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author Zyfgoup
 * @Date 2021/1/27 11:04
 * @Description
 * 树型显示分类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryVO extends Category {

    /**
     * 父级下的子分类
     */
    private List<Category> children;
}
