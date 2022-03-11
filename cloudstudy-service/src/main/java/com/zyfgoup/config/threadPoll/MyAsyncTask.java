package com.zyfgoup.config.threadPoll;
import com.zyfgoup.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
@Slf4j
public class MyAsyncTask {


    @Autowired
    StringRedisTemplate redisTemplate;

    @Async("myTaskAsyncPool")  //myTaskAsynPool即配置线程池的方法名，此处如果不写自定义线程池的方法名，会使用默认的线程池
    public void courseViewAdd(String courseId, HttpServletRequest request) throws InterruptedException{
        viewAdd(request,courseId);
    }

    /**
     * 浏览数+1 存放到redis 然后每天定时更新到数据库中
     * 每天都要把redis里 ip这个都删除
     */
    private void viewAdd(HttpServletRequest request, String courseId){
        String ip = getIpAddr(request);
        String key = ip+":"+courseId;

        //先判断这个ip+这个课程有没有点过了  如果存在了 则返回falses
        Boolean aBoolean = redisTemplate.opsForHash().putIfAbsent("ip",key,"1");

        //不存在则访问+1
        if(aBoolean) {
            redisTemplate.opsForHash().increment("view", courseId, 1);
        }
    }

    /**
     * 获取ip地址
     * @param request
     * @return
     */
    private static String getIpAddr(HttpServletRequest request) {

//        String remoteHost = request.getRemoteAddr();  简单获取ip地址


//有可能客户端的请求被代理了  要根据请求头和一些代理方式来获取
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            String localIp = "127.0.0.1";
            String localIpv6 = "0:0:0:0:0:0:0:1";
            if (ipAddress.equals(localIp) || ipAddress.equals(localIpv6)) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                    ipAddress = inet.getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        String ipSeparate = ",";
        int ipLength = 15;
        if (ipAddress != null && ipAddress.length() > ipLength) {
            if (ipAddress.indexOf(ipSeparate) > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(ipSeparate));
            }
        }
        return ipAddress;
    }
}