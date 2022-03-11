package com.zyfgoup.controller;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyfgoup.dto.PageDTO;
import com.zyfgoup.entity.AclRole;
import com.zyfgoup.entity.Admin;
import com.zyfgoup.entity.Result;
import com.zyfgoup.service.AclRoleService;
import com.zyfgoup.utils.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.acl.Acl;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-04
 */
@RestController

public class AclRoleController {

    @Autowired
    AclRoleService aclRoleService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping("/role/{id}")
    public Result get(@PathVariable("id") String id){
        if(StrUtil.isBlank(id)){
            return Result.fail("id参数不能为空");
        }

        AclRole byId = aclRoleService.getById(id);
        if(ObjectUtil.isNull(byId)){
            return Result.fail("查询失败");
        }
        return Result.succ(byId);
    }

    @PutMapping("/role/update")
    public Result update(@RequestBody AclRole role){
        if(ObjectUtil.isNull(role) || StrUtil.isBlank(role.getId())){
            return Result.fail("参数或者id不能为空");
        }

        boolean b = aclRoleService.updateById(role);
        if(!b){
            return Result.fail("更新失败");
        }
        redisTemplate.delete(RedisKey.ALL_ROLE);
        return Result.succ("更新成功");
    }


    @PutMapping("/role/save")
    public Result save(@RequestBody AclRole role){
        if(ObjectUtil.isNull(role)){
            return Result.fail("参数不能为空");
        }

        boolean b = aclRoleService.save(role);
        if(!b){
            return Result.fail("添加失败");
        }
        redisTemplate.delete(RedisKey.ALL_ROLE);
        return Result.succ("添加成功");
    }

    @DeleteMapping("/role/remove/{id}")
    public Result delete(@PathVariable("id") String id){
        if(StrUtil.isBlank(id)){
            return Result.fail("id参数不能为空");
        }
        boolean update = aclRoleService.update(new UpdateWrapper<AclRole>().eq("id", id).set("is_deleted", 1));
        if(!update){
            return Result.fail("删除失败");
        }
        redisTemplate.delete(RedisKey.ALL_ROLE);
        return Result.succ("删除成功");

    }

    @DeleteMapping("/role/batchRemove")
    public Result deleteRows(@RequestBody List<String> idList){
        if(idList.size()==0){
            return Result.fail("至少选择一条信息进行删除");
        }

        Collection<AclRole> aclRoles = aclRoleService.listByIds(idList);
        aclRoles.stream().forEach(role -> role.setIsDeleted(1));
        boolean b = aclRoleService.updateBatchById(aclRoles);

        if(!b){
            return Result.fail("删除失败");
        }
        redisTemplate.delete(RedisKey.ALL_ROLE);
        return Result.succ("删除成功");
    }


    @PostMapping("/role/get/{current}/{size}")
    public Result getPageList(@PathVariable("current") Long current,@PathVariable("size") Long size,
                              @RequestBody Map<String,String> searchObj){
        if(ObjectUtil.isNull(current)||ObjectUtil.isNull(size)){
            return Result.fail("分页参数不能为空");
        }

        if(current==0||size==0){
            return Result.fail("分页参数不能为0");
        }

        //Map
        String roleName =searchObj.get("roleName");

        //如果查询条件都为空
        //从redis
        if(StrUtil.isBlank(roleName)){
            if(!redisTemplate.hasKey(RedisKey.ALL_ROLE)) {
                List<AclRole> list = aclRoleService.list(new QueryWrapper<AclRole>().eq("is_deleted",0));
                redisTemplate.opsForValue().set(RedisKey.ALL_ROLE, JSON.toJSONString(list),1, TimeUnit.HOURS);
            }

            //都从redis拿
            String s = redisTemplate.opsForValue().get(RedisKey.ALL_ROLE);
            List<AclRole> list = JSON.parseArray(s,AclRole.class);

            // 要保证 尾部不能超过已有条数
            int start = (int)(current-1)*10;
            int end = (current*size)>list.size()?list.size():(int)(current*size);
            //取多少到多少 从0开始 左闭右开
            List<AclRole> courses = list.subList(start,end);
            PageDTO pageDTO = new PageDTO(courses,current,size,Long.valueOf(list.size()));
            return Result.succ(pageDTO);
        }

        //有查询条件
        //构建分页
        IPage page = new Page(current,size);
        QueryWrapper<AclRole> wrapper = new QueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(roleName),"role_name",roleName);
        wrapper.eq("is_deleted",0);
        aclRoleService.page(page,wrapper);
        return Result.succ(PageDTO.get(page));
    }


}
