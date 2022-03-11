package com.zyfgoup.controller;


import com.baomidou.mybatisplus.extension.api.R;
import com.github.wxpay.sdk.WXPayUtil;
import com.zyfgoup.entity.Order;
import com.zyfgoup.entity.Result;
import com.zyfgoup.exception.BaseException;
import com.zyfgoup.exception.ErrorCode;
import com.zyfgoup.service.OrderService;
import com.zyfgoup.service.PayLogService;
import com.zyfgoup.utils.ConstantWxUtils;
import com.zyfgoup.utils.HttpClient;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 支付日志表 前端控制器
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-16
 */
@RestController
@RequestMapping("/pay-log")
@Slf4j
public class PayLogController {
    @Autowired
    private PayLogService payLogService;

    @Autowired
    OrderService orderService;

    //生成微信支付二维码接口  购物车
    //参数是订单号
    @ApiOperation(value = "生成微信支付二维码接口")
    @GetMapping("createQrCodeByCart/{orderNo}")
    public Result createQrCodeByCart(@PathVariable String orderNo) {
        //返回信息，包含二维码地址，还有其他需要的信息
        Map map = payLogService.createQrCode(orderNo);
        log.info("二维码合集{}",map);
        return Result.succ(map);
    }

    /**
     * 单个课程直接购买
     * @param id
     * @return
     */
    @GetMapping("/createQrCode/{id}")
    public Result createQrCode(@PathVariable("id") String id){
        try {
            Order order = orderService.getById(id);
            //2 使用map设置生成二维码需要参数
            Map m = new HashMap();
            m.put("appid", ConstantWxUtils.WX_PAY_APPID);
            m.put("mch_id", ConstantWxUtils.WX_PAY_PARTNER);
            m.put("nonce_str", WXPayUtil.generateNonceStr());
            m.put("body", order.getCourseTitle()); //课程标题
            m.put("out_trade_no", order.getId()); //如果是在购物车里生成的订单 又在订单里单独购买 不能再使用orderNo
            m.put("total_fee", order.getTotalFee().multiply(new BigDecimal(100)).longValue() + "");
            m.put("spbill_create_ip", "127.0.0.1");
            //m.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify\n");
            m.put("notify_url", ConstantWxUtils.WX_PAY_NOTIFYURL);
            m.put("trade_type", "NATIVE");

            //3 发送httpclient请求，传递参数xml格式，微信支付提供的固定的地址
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            //设置xml格式的参数
            //client.setXmlParam(WXPayUtil.generateSignedXml(m,"T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));
            client.setXmlParam(WXPayUtil.generateSignedXml(m, ConstantWxUtils.WX_PAY_PARTNERKEY));
            client.setHttps(true);
            //执行post请求发送
            client.post();

            //4 得到发送请求返回结果
            //返回内容，是使用xml格式返回
            String xml = client.getContent();

            //把xml格式转换map集合，把map集合返回
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);

            //最终返回数据 的封装
            Map map = new HashMap();
            map.put("out_trade_no", order.getId());
            map.put("course_id", order.getCourseId());
            map.put("fromCart", false);
            map.put("total_fee", order.getTotalFee());
            map.put("result_code", resultMap.get("result_code"));  //返回二维码操作状态码
            map.put("code_url", resultMap.get("code_url"));        //二维码地址

            return Result.succ(map);
        }catch (Exception e){
            throw new BaseException(ErrorCode.WX_ERROR);
        }

    }

    //查询订单支付状态
    //参数：订单号，根据订单号查询 支付状态

    //单个支付时 传入的是订单的id  多个的时候传的是orderNo
    @ApiOperation(value = "查询订单支付状态")
    @GetMapping("queryPayStatus/{orderNo}")
    public Result queryPayStatus(@PathVariable String orderNo)  {
        Map<String,String> map = payLogService.queryPayStatus(orderNo);
        System.out.println("*****查询订单状态map集合:"+map);
        if(map == null) {
            return Result.fail("支付出错了");
        }
        //如果返回map里面不为空，通过map获取订单状态
        if(map.get("trade_state").equals("SUCCESS")) {//支付成功
            //添加记录到支付表，更新订单表订单状态
            payLogService.updateOrdersStatus(map);
            return Result.succ("支付成功",null);
        }
        return Result.succ(25000,"支付中",null);

    }
}
