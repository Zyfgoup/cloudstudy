package com.zyfgoup.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyfgoup.dto.PageDTO;
import com.zyfgoup.entity.AclAdminRole;
import com.zyfgoup.entity.AclRole;
import com.zyfgoup.entity.Admin;
import com.zyfgoup.entity.Result;
import com.zyfgoup.service.AclAdminRoleService;
import com.zyfgoup.service.AclRoleService;
import com.zyfgoup.service.AdminService;
import com.zyfgoup.utils.RedisKey;
import com.zyfgoup.words.WordFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 管理端登录用户 前端控制器
 * </p>
 *
 * @author zyfgoup
 * @since 2021-01-20
 */
@RestController
@Slf4j
public class AdminController {
    @Autowired
    AdminService adminService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    AclRoleService aclRoleService;

    @Autowired
    AclAdminRoleService adminRoleService;


    /**
     * 供auth模块验证登录
     * @param name
     * @return
     */
    @GetMapping("/out/get/{name}")
    public Map<String,Object> getByName(@PathVariable("name") String name){
        if(StrUtil.isBlank(name)){
            return null;
        }
        //未被删除的
         Admin admin = adminService.getOne(new QueryWrapper<Admin>().eq("name",name).eq("is_deleted","0"));
         if(ObjectUtil.isNull(admin)){
             Map<String,Object> res = new HashMap<>();
             res.put("admin",null);
             return res;
         }

         //获取角色
        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(admin);

        List<String> roles = adminService.getRoles4AdminId(admin.getId());
        Map<String,Object> res = new HashMap<>();
        res.put("admin",stringObjectMap);
        res.put("roles",roles);
        return res;
    }

    /**
     * 根据id返回信息
     * @param id
     * @return
     */
    @GetMapping("/admin/get/{id}")
    public Result get(@PathVariable("id")String id){
        if(StrUtil.isBlank(id)){
            return Result.fail("id不能为空或者为0");
        }
        Admin byId = adminService.getById(id);
        return Result.succ(byId);
    }

    @PostMapping("/admin/get/{current}/{size}")
    public Result getPageList(@PathVariable("current") Long current,@PathVariable("size") Long size,
                              @RequestBody Map<String,String> searchObj){
        if(ObjectUtil.isNull(current)||ObjectUtil.isNull(size)){
            return Result.fail("分页参数不能为空");
        }

        if(current==0||size==0){
            return Result.fail("分页参数不能为0");
        }

        //Map
        String name =searchObj.get("name");

        //如果查询条件都为空
        //从redis
        if(StrUtil.isBlank(name)){
            if(!redisTemplate.hasKey(RedisKey.ALL_Admin_MEMBER)) {
                List<Admin> list = adminService.list(new QueryWrapper<Admin>().eq("is_deleted",0));
                redisTemplate.opsForValue().set(RedisKey.ALL_Admin_MEMBER, JSON.toJSONString(list),1, TimeUnit.HOURS);
            }

            //都从redis拿
            String s = redisTemplate.opsForValue().get(RedisKey.ALL_Admin_MEMBER);
            List<Admin> list = JSON.parseArray(s,Admin.class);

            // 要保证 尾部不能超过已有条数
            int start = (int)(current-1)*10;
            int end = (current*size)>list.size()?list.size():(int)(current*size);
            //取多少到多少 从0开始 左闭右开
            List<Admin> courses = list.subList(start,end);
            PageDTO pageDTO = new PageDTO(courses,current,size,Long.valueOf(list.size()));
            return Result.succ(pageDTO);
        }

        //有查询条件
        //构建分页
        IPage page = new Page(current,size);
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(name),"name",name);
        wrapper.eq("is_deleted",0);
        adminService.page(page,wrapper);
        return Result.succ(PageDTO.get(page));
    }

    @PostMapping("/admin/save")
    public Result save(@RequestBody Admin admin){
        if(ObjectUtil.isNull(admin)){
            return Result.fail("参数不能为空");
        }

        Admin name = adminService.getOne(new QueryWrapper<Admin>().eq("name", admin.getName()));
        if(ObjectUtil.isNotNull(name)){
            return Result.fail("用户名已存在");
        }

        //敏感词处理
        if(WordFilter.haveSensitiveWord(admin.getName())){
            return Result.fail("名称不能含有敏感词");
        }



        //加密
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        admin.setPassword(encoder.encode(admin.getPassword()));

        boolean save = adminService.save(admin);
        if(!save){
            return Result.fail("添加失败");
        }

        redisTemplate.delete(RedisKey.ALL_Admin_MEMBER);
        return Result.succ("添加成功");
    }

    @PutMapping("/admin/update")
    public Result update(@RequestBody Admin admin){
        if(ObjectUtil.isNull(admin)){
            return Result.fail("参数不能为空");
        }
        //敏感词处理
        if(WordFilter.haveSensitiveWord(admin.getName())){
            return Result.fail("名称不能含有敏感词");
        }

        if(StrUtil.isNotBlank(admin.getPassword())){
            //加密
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            admin.setPassword(encoder.encode(admin.getPassword()));
        }

        boolean save = adminService.update(new UpdateWrapper<Admin>().eq("id",admin.getId())
        .set("name",admin.getName()).set(!StrUtil.isBlank(admin.getPassword()),"password",admin.getPassword()));
        if(!save){
            return Result.fail("更新失败");
        }

        redisTemplate.delete(RedisKey.ALL_Admin_MEMBER);
        return Result.succ("更新成功");
    }

    /**
     * 逻辑删除
     * 传入String
     * @param id
     * @return
     */
    @DeleteMapping("/admin/remove/{id}")
    public Result remove(@PathVariable("id")String id){
        if(StrUtil.isBlank(id)){
            return Result.fail("id不能为空或者为0");
        }
        boolean update = adminService.update(new UpdateWrapper<Admin>().eq("id", id).set("is_deleted", 1));
        if(!update){
            return Result.fail("删除失败");
        }
        redisTemplate.delete(RedisKey.ALL_Admin_MEMBER);
        return Result.succ("删除成功");
    }

    /**
     * 逻辑删除
     * 传入String
     * @param idList
     * @return
     */
    @DeleteMapping("/admin/batchRemove")
    public Result bacthRemove(@RequestBody List<String> idList){
        if(idList.size()==0){
            return Result.fail("至少选择一行删除");
        }
        List<Admin> admins = (List<Admin>)adminService.listByIds(idList);
        admins.stream().forEach(admin -> admin.setIsDeleted(1));
        boolean b = adminService.updateBatchById(admins);
        if(!b){
            return Result.fail("删除失败");
        }
        redisTemplate.delete(RedisKey.ALL_Admin_MEMBER);
        return Result.succ("删除成功");
    }

    /**
     * 冻结
     * @param
     * @return
     */
    @PutMapping("/admin/freeze")
    public Result freeze(@RequestBody Map<String,String>map){
        String id = map.get("id");
        if(StrUtil.isBlank(id)){
            return Result.fail("参数不能为空");
        }
        Admin byId = adminService.getById(id);
        if(byId.getIsDisabled()!=0){
           return Result.fail("该用户已经被冻结");
        }
        boolean update = adminService.update(new UpdateWrapper<Admin>().eq("id", id).set("is_disabled", 1));
        if(!update){
            return Result.fail("冻结失败");
        }
        redisTemplate.delete(RedisKey.ALL_Admin_MEMBER);
        return Result.succ(null);

    }

    /**
     * 冻结
     * @param
     * @return
     */
    @PutMapping("/admin/unfreeze")
    public Result unfreeze(@RequestBody Map<String,String>map){
        String id = map.get("id");
        if(StrUtil.isBlank(id)){
            return Result.fail("参数不能为空");
        }
        Admin byId = adminService.getById(id);
        if(byId.getIsDisabled()!=1){
            return Result.fail("该用户已经被解冻");
        }
        boolean update = adminService.update(new UpdateWrapper<Admin>().eq("id", id).set("is_disabled", 0));
        if(!update){
            return Result.fail("解冻失败");
        }
        redisTemplate.delete(RedisKey.ALL_Admin_MEMBER);
        return Result.succ(null);

    }


    /**
     *获取用户 角色信息
     * @param userId
     * @return
     */

    @GetMapping("/admin/toAssign/{userId}")
    public Result toAssign(@PathVariable String userId) {
        Map<String, Object> roleMap = new HashMap<>();
        List<AclRole> allRoles = aclRoleService.list();
        List<AclAdminRole> existsList = adminRoleService.list(new QueryWrapper<AclAdminRole>().eq("user_id", userId).select("role_id"));

        //取出id
        List<String> roleIds = existsList.stream().map(c -> c.getRoleId()).collect(Collectors.toList());

        //对角色进行分类
        List<AclRole> assignRoles = new ArrayList<>();
        for (AclRole role : allRoles) {
            //已分配
            if(roleIds.contains(role.getId())) {
                assignRoles.add(role);
            }
        }
        roleMap.put("assignRoles", assignRoles);
        roleMap.put("allRolesList", allRoles);
        return Result.succ(roleMap);
    }


    /**
     * 根据用户分配角色
     * @param userId
     * @param roleIds
     * @return
     */
    @Transactional
    @PostMapping("/admin/doAssign")
    public Result doAssign(@RequestParam String userId,@RequestParam String[] roleIds) {

        //先删除已分配的
        boolean remove = adminRoleService.remove(new QueryWrapper<AclAdminRole>().eq("user_id", userId));
        List<AclAdminRole> userRoleList = new ArrayList<>();
        for(String roleId : roleIds) {
            if(StringUtils.isEmpty(roleId)) {
                continue;
            }
            AclAdminRole aclAdminRole = new AclAdminRole();
            aclAdminRole.setUserId(userId);
            aclAdminRole.setRoleId(roleId);
            userRoleList.add(aclAdminRole);
        }
        boolean b = adminRoleService.saveBatch(userRoleList);

        if(!remove||!b){
            return Result.fail("分配失败");
        }
        return Result.succ(null);
    }






}
