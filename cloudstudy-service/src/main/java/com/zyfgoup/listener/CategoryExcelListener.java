package com.zyfgoup.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zyfgoup.entity.Category;
import com.zyfgoup.entity.excel.CategoryData;
import com.zyfgoup.exceImpl.EmptyCateFileException;
import com.zyfgoup.service.CategoryService;

public class CategoryExcelListener extends AnalysisEventListener<CategoryData> {

    //因为SubjectExcelListener不能交给spring进行管理，需要自己new，不能注入其他对象
    //不能实现数据库操作
    //手动注入
    public CategoryService categoryService;
    public CategoryExcelListener() {}  //无参
    public CategoryExcelListener(CategoryService categoryService) {  //有参
        this.categoryService= categoryService;
    }

    //读取excel内容，一行一行进行读取
    @Override
    public void invoke(CategoryData categoryData, AnalysisContext analysisContext) {
        if(categoryData == null) {
            //抛出异常
            throw new EmptyCateFileException();
        }

        //一行一行读取，每次读取有两个值，第一个值一级分类，第二个值二级分类
        //判断一级分类是否重复
        Category existOneSubject = this.existOneSubject(categoryService, categoryData.getOneSubjectName());
        if(existOneSubject == null) { //没有相同一级分类，进行添加
            existOneSubject = new Category();
            existOneSubject.setParentId("0");
            existOneSubject.setTitle(categoryData.getOneSubjectName());//一级分类名称
            categoryService.save(existOneSubject);
        }

        //获取一级分类id值 作为二级分类的父id
        String pid = existOneSubject.getId();
        //添加二级分类
        //判断二级分类是否重复
        Category existTwoSubject = this.existTwoSubject(categoryService, categoryData.getTwoSubjectName(), pid);
        if(existTwoSubject == null) {
            existTwoSubject = new Category();
            existTwoSubject.setParentId(pid);
            existTwoSubject.setTitle(categoryData.getTwoSubjectName());//二级分类名称
            categoryService.save(existTwoSubject);
        }
    }

    //判断一级分类不能重复添加
    private Category existOneSubject(CategoryService subjectService,String name) {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("title",name);
        wrapper.eq("parent_id","0");
        Category oneSubject = subjectService.getOne(wrapper);
        return oneSubject;
    }

    //判断二级分类不能重复添加
    private Category existTwoSubject(CategoryService categoryService,String name,String pid) {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("title",name);
        wrapper.eq("parent_id",pid);
        Category twoSubject = categoryService.getOne(wrapper);
        return twoSubject;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
