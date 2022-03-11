package com.zyfgoup.controller;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyfgoup.client.EduServiceClient;
import com.zyfgoup.client.UserClient;
import com.zyfgoup.entity.*;
import com.zyfgoup.entity.dto.PageDTO;
import com.zyfgoup.exception.BaseException;
import com.zyfgoup.exception.ErrorCode;
import com.zyfgoup.service.CartService;
import com.zyfgoup.service.OrderService;
import com.zyfgoup.utils.JwtUtils;
import com.zyfgoup.utils.OrderNoUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.smartcardio.Card;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-16
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    UserClient userClient;

    @Autowired
    EduServiceClient eduServiceClient;

    @Autowired
    CartService cartService;

    @Autowired
    AsyncTask asyncTask;

    @PostMapping("/list/{current}/{size}")
    public Result get(@PathVariable("current") Long current,@PathVariable("size") Long size,@RequestBody Map<String,String> searchObj) {
        //Map
        String courseId = searchObj.get("courseId");
        String teacherName = searchObj.get("teacherName");
        String begin = searchObj.get("begin");
        String userId = searchObj.get("userId");


        if (ObjectUtil.isNull(current) || ObjectUtil.isNull(size)) {
            return Result.fail("分页参数不能为空");
        }

        if (current == 0 || size == 0) {
            return Result.fail("分页参数不能为0");
        }
        IPage page = new Page(current, size);

        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq(StrUtil.isNotBlank(courseId), "course_id", courseId);
        wrapper.eq(StrUtil.isNotBlank(teacherName), "teacher_name", teacherName);
        wrapper.eq(StrUtil.isNotBlank(userId), "user_id", userId);
        wrapper.eq("is_deleted",0);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //时间查询条件
        if (StrUtil.isNotBlank(begin)) {
            LocalDateTime startTime = LocalDateTime.parse(begin, dateTimeFormatter);
            wrapper.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + startTime + "')");
        }

        orderService.page(page, wrapper);
        return Result.succ(PageDTO.get(page));

    }

    /**
     * 逻辑删除
     * @param id
     * @return
     */
    @DeleteMapping("/remove/{id}")
    public Result delete(@PathVariable("id") String id){
        if(StrUtil.isBlank(id)){
            return Result.fail("参数不能为空");
        }

        boolean save = orderService.update(new UpdateWrapper<Order>().eq("id",id).set("is_deleted",1));
        if(!save){
            return Result.fail("删除失败");
        }
        return Result.succ(null);

    }




    //1 生成订单的方法
    @ApiOperation(value = "生成订单")
    @PostMapping("createOrder/{courseId}")
    public Result saveOrder(@PathVariable String courseId, HttpServletRequest request) throws InterruptedException {
        //创建订单，返回订单号
        String token = request.getHeader("Authorization");
        if(StrUtil.isBlank(token)){
            throw new BaseException(ErrorCode.TOKEN_EMPTY);
        }
        UserVO userVo = JwtUtils.getUserVo(token);

        if(ObjectUtil.isNull(userVo)){
            throw new BaseException(ErrorCode.TOKEN_ERROR);
        }


        //已经有这个订单了 不需要重新生成
        Order one = orderService.getOne(new QueryWrapper<Order>().eq("course_id", courseId).eq("user_id", userVo.getId())
                .eq("is_deleted", 0));
        if(one !=null){
            return Result.succ(one.getId());
        }


        //通过远程调用根据用户id获取用户信息
        Result result = userClient.outGetOne(userVo.getId());
        if(result.getCode() != 200){
            return Result.fail("生成订单失败");
        }
        Map<String,Object> map = (Map<String, Object>) result.getData();

        //通过远程调用根据课程id获取课信息
        CourseWebVoOrder courseInfoOrder = eduServiceClient.getCourseInfoOrder(courseId);

        //创建Order对象，向order对象里面设置需要数据
        Order order = new Order();
        order.setOrderNo(OrderNoUtil.getOrderNo());//订单号
        order.setCourseId(courseId); //课程id
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

        return Result.succ(order.getId());
    }

    //1 生成订单的方法
    @ApiOperation(value = "生成订单")
    @PostMapping("/createOrderByCart")
    public Result saveOrderByCart(@RequestBody List<Cart> cartList, HttpServletRequest request) throws InterruptedException {
        //创建订单，返回订单号
        String token = request.getHeader("Authorization");
        if(StrUtil.isBlank(token)){
            throw new BaseException(ErrorCode.TOKEN_EMPTY);
        }
        UserVO userVo = JwtUtils.getUserVo(token);

        if(ObjectUtil.isNull(userVo)){
            throw new BaseException(ErrorCode.TOKEN_ERROR);
        }

        List<String> courseIds = cartList.stream().map(cart -> cart.getCourseId()).collect(Collectors.toList());


        //通过远程调用根据用户id获取用户信息
        Result result = userClient.outGetOne(userVo.getId());
        if(result.getCode() != 200){
            return Result.fail("生成订单失败");
        }
        Map<String,Object> map = (Map<String, Object>) result.getData();

        //通过远程调用根据课程id获取课信息
        System.out.println("---------"+Thread.currentThread().getName());
        String orderNo = OrderNoUtil.getOrderNo();
        for (String courseId : courseIds) {
            //多线程去存
            asyncTask.getCourseInfoOrder(courseId,orderNo,map);
        }



        return Result.succ(orderNo);

    }

    //2 根据订单id查询订单信息
    @ApiOperation(value = "根据订单id查询订单信息")
    @GetMapping("getOrderInfo/{id}")
    public Result getOrderInfo(@PathVariable("id") String id) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);
        Order order = orderService.getOne(wrapper);
        return Result.succ(order);
    }


    //查询订单表中订单的状态 判断课程是否购买
    @ApiOperation( "判断课程是否购买")
    @GetMapping("isBought/{courseId}")
    public Result isBuyByCourseId(@PathVariable String courseId, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if(StrUtil.isBlank(token)){
            throw new BaseException(ErrorCode.TOKEN_EMPTY);
        }
        UserVO userVo = JwtUtils.getUserVo(token);

        if(ObjectUtil.isNull(userVo)){
            throw new BaseException(ErrorCode.TOKEN_ERROR);
        }
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("user_id", userVo.getId())
                .eq("course_id", courseId)
                .eq("status", 1);
        Integer count = orderService.count(queryWrapper);
        if(count>0)
            return Result.succ(true);
        else
            return Result.succ(false);
    }

    //获取当前用户订单列表
    @ApiOperation(value = "获取当前用户订单列表")
    @GetMapping("orderList")
    public Result list(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if(StrUtil.isBlank(token)){
            throw new BaseException(ErrorCode.TOKEN_EMPTY);
        }
        UserVO userVo = JwtUtils.getUserVo(token);

        if(ObjectUtil.isNull(userVo)){
            throw new BaseException(ErrorCode.TOKEN_ERROR);
        }
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .orderByDesc("create_time")
                .eq("user_id", userVo.getId())
                .eq("is_deleted",0);

        List<Order> list = orderService.list(queryWrapper);
        return Result.succ(list);
    }

    //生成订单中使用多线程
    //等到订单都生成 才去请求生成二维码
    //如果返回长度大于0  表示订单生成
    //把 cart也删除
    @ApiOperation(value = "获取当前用户订单列表")
    @GetMapping("/orderListByOrderNo/{orderNo}")
    public Result listOrderNo(@PathVariable("orderNo") String orderNo, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if(StrUtil.isBlank(token)){
            throw new BaseException(ErrorCode.TOKEN_EMPTY);
        }
        UserVO userVo = JwtUtils.getUserVo(token);

        if(ObjectUtil.isNull(userVo)){
            throw new BaseException(ErrorCode.TOKEN_ERROR);
        }
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("user_id", userVo.getId())
                .eq("order_no",orderNo)
                .eq("is_deleted",0);

        List<Order> list = orderService.list(queryWrapper);
        if(list.size()>0){
            //删除购物车
            cartService.remove(new QueryWrapper<Cart>().eq("user_id",userVo.getId()));
            return Result.succ(true);
        }
        return Result.succ(false);
    }

    @ApiOperation(value = "删除订单")
    @DeleteMapping("removeOrder/{orderId}")
    public Result remove(@PathVariable String orderId, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if(StrUtil.isBlank(token)){
            throw new BaseException(ErrorCode.TOKEN_EMPTY);
        }
        UserVO userVo = JwtUtils.getUserVo(token);

        if(ObjectUtil.isNull(userVo)){
            throw new BaseException(ErrorCode.TOKEN_ERROR);
        }

        //逻辑删除
        boolean result = orderService.update(new UpdateWrapper<Order>().eq("id",orderId)
        .eq("user_id",userVo.getId()).set("is_deleted",1));
        if(result){
            return Result.succ("删除订单成功",null);
        }else{
            return Result.fail("数据不存在");
        }
    }


}
