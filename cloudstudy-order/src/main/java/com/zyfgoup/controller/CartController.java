package com.zyfgoup.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zyfgoup.entity.Cart;
import com.zyfgoup.entity.Result;
import com.zyfgoup.entity.UserVO;
import com.zyfgoup.exception.BaseException;
import com.zyfgoup.exception.ErrorCode;
import com.zyfgoup.service.CartService;
import com.zyfgoup.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-18
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    CartService cartService;


    @PostMapping("/add")
    public Result addCart(@RequestBody Map<String,String> map, HttpServletRequest request){
        //创建订单，返回订单号
        String token = request.getHeader("Authorization");
        if(StrUtil.isBlank(token)){
            throw new BaseException(ErrorCode.TOKEN_EMPTY);
        }
        UserVO userVo = JwtUtils.getUserVo(token);

        if(ObjectUtil.isNull(userVo)){
            throw new BaseException(ErrorCode.TOKEN_ERROR);
        }

        Cart cart1 = new Cart();
        cart1.setCover(map.get("cover"));
        cart1.setTitle(map.get("title"));
        cart1.setPrice(BigDecimal.valueOf(Double.valueOf(map.get("price"))));
        cart1.setTeacherId(map.get("teacherId"));
        cart1.setTeacherName(map.get("teacherName"));
        cart1.setUserId(userVo.getId());
        cart1.setCourseId(map.get("id"));

        Cart one = cartService.getOne(new QueryWrapper<Cart>().eq("course_id", cart1.getCourseId()).eq("user_id", cart1.getUserId()));
        if(one !=null){
            return Result.fail("该课程已在购物车");
        }
        cartService.save(cart1);
        return  Result.succ(null);
    }


    @GetMapping("/list")
    public Result getListByUser(HttpServletRequest request){
        //创建订单，返回订单号
        String token = request.getHeader("Authorization");
        if(StrUtil.isBlank(token)){
            throw new BaseException(ErrorCode.TOKEN_EMPTY);
        }
        UserVO userVo = JwtUtils.getUserVo(token);

        if(ObjectUtil.isNull(userVo)){
            throw new BaseException(ErrorCode.TOKEN_ERROR);
        }
        List<Cart> list = cartService.list(new QueryWrapper<Cart>().eq("user_id", userVo.getId()));
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Cart cart : list) {
            totalPrice  = totalPrice.add(cart.getPrice());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("cartList",list);
        map.put("amount",list.size());
        map.put("totalPrice",totalPrice);
        return Result.succ(map);
    }

    @DeleteMapping("/remove/{id}")
    public Result remove(@PathVariable("id") String id){
        boolean b = cartService.removeById(id);
        if(!b){
            return Result.fail("删除失败");
        }
        return Result.succ("操作成功");
    }

    //课程被删除了 那么购物车里的也要被删除

    @DeleteMapping("/remove/courseId/{id}")
    public Result removeByCourseId(@PathVariable("id") String id){
        boolean b = cartService.remove(new QueryWrapper<Cart>().eq("course_id",id));
        if(!b){
            return Result.fail("删除失败");
        }
        return Result.succ("操作成功");
    }



}
