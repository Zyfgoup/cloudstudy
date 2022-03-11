package com.zyfgoup.service;

import com.zyfgoup.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 分类 服务类
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-26
 */
public interface CategoryService extends IService<Category> {

    //添加课程分类
    void importCategory(MultipartFile file,CategoryService categorySevice);

}
