package com.zyfgoup.entity.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @Author Zyfgoup
 * @Date 2021/3/1 19:34
 * @Description
 */
@Data
public class CategoryData{

    //1级分类
    @ExcelProperty(index = 0)
    private String oneSubjectName;

    //2级分类
    @ExcelProperty(index = 1)
    private String twoSubjectName;
}

