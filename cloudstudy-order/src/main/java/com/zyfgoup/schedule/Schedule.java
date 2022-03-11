package com.zyfgoup.schedule;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zyfgoup.entity.Order;
import com.zyfgoup.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;


/**
 * @Author Zyfgoup
 * @Date 2021/2/3 16:36
 * @Description
 */
@Component
@Slf4j
public class Schedule {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    OrderService orderService;


    /**
     * 20分钟查一次如果是 未支付的订单 大于15分钟则删除
     */
    @Scheduled(cron = "0 0/20 * * * ?")
    public void courseView() {
        log.info("删除未支付时间大于15分钟的");
        List<Order> status = orderService.list(new QueryWrapper<Order>().eq("status", 0));
        for (Order order : status) {
            LocalDateTime createTime = order.getCreateTime();
            //大于15分钟
            if (createTime.plusMinutes(15).compareTo(LocalDateTime.now()) <= 0) {
                orderService.removeById(order.getId());
            }


        }
    }



}
