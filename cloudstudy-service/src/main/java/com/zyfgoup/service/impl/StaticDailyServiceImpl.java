package com.zyfgoup.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.zyfgoup.client.UserClient;
import com.zyfgoup.entity.Result;
import com.zyfgoup.entity.StaticDaily;
import com.zyfgoup.mapper.StaticDailyMapper;
import com.zyfgoup.service.StaticDailyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zyfgoup.utils.RedisKey;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 网站统计日数据 服务实现类
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-10
 */
@Service
public class StaticDailyServiceImpl extends ServiceImpl<StaticDailyMapper, StaticDaily> implements StaticDailyService {

    @Autowired
    UserClient userClient;

    @Autowired
    StringRedisTemplate redisTemplate;


    /**
     * 生成指定某天的数据 其实每天的数据都已经生成过了 更多的是更新当天的数据
     * @param day
     * @return
     */
    @Override
    public boolean createNowData(String day) {

        //远程调用得到某一天的注册人数
        Result register = userClient.countRegister(day);
        Integer countRegister = 0;
        if(register.getCode() == 200) {
            countRegister = (Integer) register.getData();
        }else{
         return false;
        }


        /**
         * 正常操作应该去逐个查表 获取新的数据 更新
         * 数据不够多 就模拟了
         */


        //获取播放数量
        //Integer videoViewNum = Integer.valueOf(redisTemplate.opsForValue().get(RedisKey.VIDEO_PLAY_NUM));

        //看今天的数据生成没  可以提前生成
        //有的话  覆盖
        int i = 0;
        StaticDaily date_calculated = baseMapper.selectOne(new QueryWrapper<StaticDaily>().eq("date_calculated", day));
        if(ObjectUtil.isNotNull(date_calculated)){
            //把获取到的数据添加到数据库 统计分析表内
            StaticDaily statisticsDaily = new StaticDaily();
            statisticsDaily.setRegisterNum(date_calculated.getRegisterNum()+5); //注册人数
            statisticsDaily.setDateCalculated(day); //统计日期

            //如果生成了在原来的基础上改动 模拟数据
            statisticsDaily.setVideoViewNum(date_calculated.getVideoViewNum()+3);
            statisticsDaily.setLoginNum(date_calculated.getLoginNum()+5);
            statisticsDaily.setCourseNum(date_calculated.getCourseNum());
            statisticsDaily.setId(date_calculated.getId());
            i =  baseMapper.updateById(statisticsDaily);
        }else {
            //把获取到的数据添加到数据库 统计分析表内
            StaticDaily statisticsDaily = new StaticDaily();
            if(countRegister != 0)
                statisticsDaily.setRegisterNum(countRegister); //注册人数
            else
                statisticsDaily.setRegisterNum(RandomUtils.nextInt(100, 200));
            statisticsDaily.setDateCalculated(day); //统计日期
            //随机数
            statisticsDaily.setVideoViewNum(RandomUtils.nextInt(100, 200));
            statisticsDaily.setLoginNum(RandomUtils.nextInt(100, 200));
            statisticsDaily.setCourseNum(RandomUtils.nextInt(100, 200));
            i = baseMapper.insert(statisticsDaily);
        }

        //看今天的数据生成没  可以提前生成
        if(i<=0) {
            return false;
        }
        return true;
    }



    //图表显示 返回两部分数据：日期json数组、数量json数组
    @Override
    public Map<String, Object> getShowData(String type, String begin, String end) {
        //根据条件查询对应的数据
        QueryWrapper<StaticDaily> wrapper = new QueryWrapper<>();
        wrapper.between("date_calculated",begin,end);
        wrapper.select("date_calculated",type);
        wrapper.orderByAsc("date_calculated");
        List<StaticDaily> statisticsDailies = baseMapper.selectList(wrapper);

        //返回的数据：1.日期 2.日期对应的结构
        //前端json数组对应后端的list集合
        //创建日期list 数量list
        List<String> date_calculatedList = new ArrayList<>();
        List<Integer> numDataList = new ArrayList<>();

        //遍历查询所有数据的list集合statisticsDailies 进行封装
        for (int i = 0; i < statisticsDailies.size(); i++) {
            StaticDaily daily = statisticsDailies.get(i);
            //封装日期list集合
            date_calculatedList.add(daily.getDateCalculated());
            //封装对应的具体数量（登录 注册 播放等）
            switch (type){
                case "login_num":
                    numDataList.add(daily.getLoginNum());
                    break;
                case "register_num":
                    numDataList.add(daily.getRegisterNum());
                    break;
                case "video_view_num":
                    numDataList.add(daily.getVideoViewNum());
                    break;
                case "course_num":
                    numDataList.add(daily.getCourseNum());
                    break;
                default:
                    break;
            }
        }

        //把封装之后两个list集合放到map集合，进行返回
        Map<String, Object> map = new HashMap<>();
        map.put("date_calculatedList",date_calculatedList);
        map.put("numDataList",numDataList);
        return map;
    }

    @Override
    public void create7Data() {

        redisTemplate.delete(RedisKey.SERVEN_DAY_DATA);

        //获取7天的数据
        StaticDaily date_calculated1 = baseMapper.selectOne(new QueryWrapper<StaticDaily>().eq("date_calculated", DateUtil.format(DateUtil.offsetDay(DateUtil.date(), -1),"yyyy-MM-dd")));
        StaticDaily date_calculated2 = baseMapper.selectOne(new QueryWrapper<StaticDaily>().eq("date_calculated", DateUtil.format(DateUtil.offsetDay(DateUtil.date(), -2),"yyyy-MM-dd")));
        StaticDaily date_calculated3 = baseMapper.selectOne(new QueryWrapper<StaticDaily>().eq("date_calculated", DateUtil.format(DateUtil.offsetDay(DateUtil.date(), -3),"yyyy-MM-dd")));
        StaticDaily date_calculated4 = baseMapper.selectOne(new QueryWrapper<StaticDaily>().eq("date_calculated", DateUtil.format(DateUtil.offsetDay(DateUtil.date(), -4),"yyyy-MM-dd")));
        StaticDaily date_calculated5 = baseMapper.selectOne(new QueryWrapper<StaticDaily>().eq("date_calculated", DateUtil.format(DateUtil.offsetDay(DateUtil.date(), -5),"yyyy-MM-dd")));
        StaticDaily date_calculated6 = baseMapper.selectOne(new QueryWrapper<StaticDaily>().eq("date_calculated", DateUtil.format(DateUtil.offsetDay(DateUtil.date(), -6),"yyyy-MM-dd")));
        StaticDaily date_calculated7 = baseMapper.selectOne(new QueryWrapper<StaticDaily>().eq("date_calculated", DateUtil.format(DateUtil.offsetDay(DateUtil.date(), -7),"yyyy-MM-dd")));


        List<String> date = new ArrayList<>();
        date.add(date_calculated7.getDateCalculated());
        date.add(date_calculated6.getDateCalculated());
        date.add(date_calculated5.getDateCalculated());
        date.add(date_calculated4.getDateCalculated());
        date.add(date_calculated3.getDateCalculated());
        date.add(date_calculated2.getDateCalculated());
        date.add(date_calculated1.getDateCalculated());

        List<Integer> registNum = new ArrayList<>();
        registNum.add(date_calculated7.getRegisterNum());
        registNum.add(date_calculated6.getRegisterNum());
        registNum.add(date_calculated5.getRegisterNum());
        registNum.add(date_calculated4.getRegisterNum());
        registNum.add(date_calculated3.getRegisterNum());
        registNum.add(date_calculated2.getRegisterNum());
        registNum.add(date_calculated1.getRegisterNum());

        List<Integer> loginNum = new ArrayList<>();
        loginNum.add(date_calculated7.getLoginNum());
        loginNum.add(date_calculated6.getLoginNum());
        loginNum.add(date_calculated5.getLoginNum());
        loginNum.add(date_calculated4.getLoginNum());
        loginNum.add(date_calculated3.getLoginNum());
        loginNum.add(date_calculated2.getLoginNum());
        loginNum.add(date_calculated1.getLoginNum());

        List<Integer> videoViewNum = new ArrayList<>();
        videoViewNum.add(date_calculated7.getVideoViewNum());
        videoViewNum.add(date_calculated6.getVideoViewNum());
        videoViewNum.add(date_calculated5.getVideoViewNum());
        videoViewNum.add(date_calculated4.getVideoViewNum());
        videoViewNum.add(date_calculated3.getVideoViewNum());
        videoViewNum.add(date_calculated2.getVideoViewNum());
        videoViewNum.add(date_calculated1.getVideoViewNum());

        List<Integer> courseNum = new ArrayList<>();
        courseNum.add(date_calculated7.getCourseNum());
        courseNum.add(date_calculated6.getCourseNum());
        courseNum.add(date_calculated5.getCourseNum());
        courseNum.add(date_calculated4.getCourseNum());
        courseNum.add(date_calculated3.getCourseNum());
        courseNum.add(date_calculated2.getCourseNum());
        courseNum.add(date_calculated1.getCourseNum());

        Map<String, Object> map = new HashMap<>();
        map.put("date",date);
        map.put("registerNum",registNum);
        map.put("loginNum",loginNum);
        map.put("videoViewNum",videoViewNum);
        map.put("courseNum",courseNum);

        redisTemplate.opsForValue().set(RedisKey.SERVEN_DAY_DATA, JSON.toJSONString(map),1, TimeUnit.DAYS);

    }
}
