package com.zyfgoup.entity;
import com.zyfgoup.client.EduServiceClient;
import com.zyfgoup.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class AsyncTask {

    @Autowired
    EduServiceClient eduServiceClient;

    @Autowired
    OrderService orderService;

    @Async("myTaskAsyncPool")  //myTaskAsynPool即配置线程池的方法名，此处如果不写自定义线程池的方法名，会使用默认的线程池
    public void addCourseBuyNum(String courseId){
            eduServiceClient.updateBuyCountById(courseId);
    }

    @Async("myTaskAsyncPool")  //myTaskAsynPool即配置线程池的方法名，此处如果不写自定义线程池的方法名，会使用默认的线程池
    public void getCourseInfoOrder(String courseId, String orderNo, Map<String,Object> map) throws InterruptedException{
        CourseWebVoOrder courseInfoOrder = eduServiceClient.getCourseInfoOrder(courseId);
        //创建Order对象，向order对象里面设置需要数据
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setCourseId(courseInfoOrder.getId()); //课程id
        order.setCourseTitle(courseInfoOrder.getTitle());
        order.setCourseCover(courseInfoOrder.getCover());
        order.setTeacherName(courseInfoOrder.getTeacherName());
        order.setTotalFee(courseInfoOrder.getPrice());
        order.setUserId((String) map.get("id"));
        order.setMobile((String) map.get("mobile"));
        order.setNickname((String) map.get("nickname"));
        order.setStatus(0);  //订单状态（0：未支付 1：已支付）
        order.setPayType(1);  //支付类型 ，微信1
        orderService.save(order);
        //开启线程 更新课程销量
       addCourseBuyNum(order.getCourseId());
    }

    public void updateOrderStatus(Order order){
        order.setStatus(1);//1代表已经支付
        orderService.updateById(order);
    }

}