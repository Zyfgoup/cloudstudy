package com.zyfgoup.service.impl;

import com.alibaba.excel.EasyExcel;
import com.zyfgoup.entity.Category;
import com.zyfgoup.entity.excel.CategoryData;
import com.zyfgoup.listener.CategoryExcelListener;
import com.zyfgoup.mapper.CategoryMapper;
import com.zyfgoup.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * <p>
 * 分类 服务实现类
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-26
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public void importCategory(MultipartFile file, CategoryService categorySevice) {
        try {
            //文件输入流
            InputStream in = file.getInputStream();
            //调用方法进行读取
            EasyExcel.read(in, CategoryData.class,new CategoryExcelListener(categorySevice)).sheet().doRead();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
