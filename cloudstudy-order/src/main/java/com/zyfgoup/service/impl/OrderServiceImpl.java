package com.zyfgoup.service.impl;

import com.zyfgoup.entity.Order;
import com.zyfgoup.mapper.OrderMapper;
import com.zyfgoup.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-16
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

}
