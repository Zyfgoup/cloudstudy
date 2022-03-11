package com.zyfgoup.service.impl;

import com.zyfgoup.entity.Cart;
import com.zyfgoup.mapper.CartMapper;
import com.zyfgoup.service.CartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-18
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

}
