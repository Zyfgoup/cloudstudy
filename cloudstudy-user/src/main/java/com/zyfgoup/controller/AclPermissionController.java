package com.zyfgoup.controller;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zyfgoup.entity.AclPermission;
import com.zyfgoup.entity.AclRolePermission;
import com.zyfgoup.entity.Admin;
import com.zyfgoup.entity.Result;
import com.zyfgoup.service.AclPermissionService;
import com.zyfgoup.service.AclRolePermissionService;
import com.zyfgoup.service.AdminService;
import com.zyfgoup.utils.MenuHelper;
import com.zyfgoup.utils.PermissionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * <p>
 * 权限 前端控制器
 * </p>
 *
 * @author zyfgoup
 * @since 2021-03-04
 */
@RestController
@RequestMapping("/permission")
public class AclPermissionController {
    @Autowired
    AclPermissionService permissionService;

    @Autowired
    AdminService adminService;

    @Autowired
    AclRolePermissionService rolePermissionService;


    /**
     * 获取url权限
     * @param name
     * @return
     */
    @PostMapping("/out/url")
    public Result getPermissionUrl(@RequestBody String name) {
        if (StrUtil.isBlank(name)) {
            return Result.fail("获取权限失败,用户名不能为空");
        }

        List<AclPermission> list = null;
        Admin admin = adminService.getOne(new QueryWrapper<Admin>().eq("name", name));

        if (ObjectUtil.isNull(admin)) {
            return Result.fail("获取权限失败,用户不存在");
        }
        //是否是超级管理员
        if ("admin".equals(name) || "1000000000000000000".equals(admin.getId())) {
            list = permissionService.list();
        } else {
            //多角色 已经去重了
            list = permissionService.selectPermissionByUserId(admin.getId());
        }

        //set去重
        Set<String> urls = new HashSet<>();
        for (AclPermission aclPermission : list) {
            //不为空
            if(StrUtil.isNotBlank(aclPermission.getUrl())) {
                String url = aclPermission.getUrl();
                if (url.indexOf(',') > 0) {
                    String[] s = url.split(",");
                    for (String s1 : s) {
                        urls.add(s1);
                    }
                } else {
                    urls.add(url);
                }
            }
        }
        return Result.succ(urls);
    }

    /**
     * 根据登录用户名 获取菜单
     * @param name
     * @return
     */
    @GetMapping("/menu/{name}")
    public Result getMenu(@PathVariable("name") String name){
        if(StrUtil.isBlank(name)){
            return Result.fail("获取权限失败,用户名不能为空");
        }

        List<AclPermission> list = null;
        Admin admin = adminService.getOne(new QueryWrapper<Admin>().eq("name", name));

        if(ObjectUtil.isNull(admin)){
            return Result.fail("获取权限失败,用户不存在");
        }
        list = permissionService.selectPermissionByUserId(admin.getId());

        List<AclPermission> permissionList = PermissionHelper.build(list);
        List<JSONObject> result = MenuHelper.bulid(permissionList);
        return Result.succ(result);
    }

    /**
     * 查询所有菜单
     * @return
     */
    @GetMapping("/all")
    public Result indexAllPermission() {
        //1 查询菜单表所有数据
        QueryWrapper<AclPermission> wrapper = new QueryWrapper<>();
        List<AclPermission> permissionList = permissionService.list(wrapper);
        //2 把查询所有菜单list集合按照要求进行封装
        List<AclPermission> resultList = bulidPermission(permissionList);
        Map<String,List<AclPermission>> map = new HashMap<>();
        map.put("children",resultList);
        return Result.succ(map);
    }

    /**
     * 递归删除菜单
     * @param id
     * @return
     */
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable String id) {
        if(StrUtil.isBlank(id)){
            return Result.fail("删除菜单id不能为空");
        }
        //1 创建list集合，用于封装所有删除菜单id值
        List<String> idList = new ArrayList<>();
        //2 向idList集合设置删除菜单id
        this.selectPermissionChildById(id,idList);
        //把当前id封装到list里面
        idList.add(id);
        boolean b = permissionService.removeByIds(idList);
        if(!b){
            return Result.fail("删除失败");
        }
        return Result.succ(null);
    }

    /**
     * 给角色分配菜单、按钮权限
     * @param roleId
     * @param permissionIds
     * @return
     */
    @Transactional
    @PostMapping("/doAssign")
    public Result doAssign(String roleId,String[] permissionIds) {
        if(StrUtil.isBlank(roleId) || permissionIds.length == 0){
            return Result.fail("角色id、菜单id不能为空");
        }
        //把原来的权限删除了
        boolean remove = rolePermissionService.remove(new QueryWrapper<AclRolePermission>().eq("role_id", roleId));
        //roleId角色id
        //permissionId菜单id 数组形式
        //1 创建list集合，用于封装添加数据
        List<AclRolePermission> rolePermissionList = new ArrayList<>();
        //遍历所有菜单数组
        for(String perId : permissionIds) {
            //RolePermission对象
            AclRolePermission rolePermission = new AclRolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(perId);
            //封装到list集合
            rolePermissionList.add(rolePermission);
        }
        //添加到角色菜单关系表
        rolePermissionService.saveBatch(rolePermissionList);
        return Result.succ(null);
    }

    /**
     * 根据角色获取菜单
     * @param roleId
     * @return
     */
    @GetMapping("toAssign/{roleId}")
    public Result toAssign(@PathVariable String roleId) {
        List<AclPermission> allPermissionList = permissionService.list();

        //根据角色id获取角色权限
        List<AclRolePermission> rolePermissionList = rolePermissionService.list(new QueryWrapper<AclRolePermission>().eq("role_id",roleId));

        // 权限已被分配的做标记
        for (int i = 0; i < allPermissionList.size(); i++) {
            AclPermission permission = allPermissionList.get(i);
            for (int m = 0; m < rolePermissionList.size(); m++) {
                AclRolePermission rolePermission = rolePermissionList.get(m);
                if(rolePermission.getPermissionId().equals(permission.getId())) {
                    permission.setSelect(true);
                }
            }
        }

        //构建递归菜单
        List<AclPermission> permissionList = bulid(allPermissionList);
        Map<String,Object> map = new HashMap<>();
        map.put("children",permissionList);
        return Result.succ(map);
    }

    /**
     * 根据用户名获取权限id
     * @param name
     * @return
     */
    @GetMapping("/get/buttons/{name}")
    public Result getButtons(@PathVariable("name") String name){
        if(StrUtil.isBlank(name)){
            return Result.fail("获取按钮权限失败,用户名不能为空");
        }
        Admin admin = adminService.getOne(new QueryWrapper<Admin>().eq("name", name));
        if(ObjectUtil.isNull(admin)){
            return Result.fail("获取权限按钮失败,用户不存在");
        }


        List<String> permissionValueList = new ArrayList<>();

        permissionValueList = permissionService.selectPermissionValueByUserId(admin.getId());

        return Result.succ(permissionValueList);

    }



    @PostMapping("save")
    public Result save(@RequestBody AclPermission permission) {
        permissionService.save(permission);
        return Result.succ(null);
    }

    @PutMapping("update")
    public Result updateById(@RequestBody AclPermission permission) {
        permissionService.updateById(permission);
        return Result.succ(null);
    }



    /**
     * 把所有菜单进行封装
     * @param permissionList
     * @return
     */
    public static List<AclPermission> bulidPermission(List<AclPermission> permissionList) {

        //创建list集合，用于数据最终封装
        List<AclPermission> finalNode = new ArrayList<>();
        //把所有菜单list集合遍历，得到顶层菜单 pid=0菜单，设置level是1
        for(AclPermission permissionNode : permissionList) {
            //得到顶层菜单 pid=0菜单
            if("0".equals(permissionNode.getPid())) {
                //设置顶层菜单的level是1
                permissionNode.setLevel(1);
                //根据顶层菜单，向里面进行查询子菜单，封装到finalNode里面
                //permissionNode遍历出的一级菜单对象-递归入口
                //permissionList查询出来的所有菜单
                //finalNode最终集合 存放好树形结构的数据
                finalNode.add(selectChildren(permissionNode,permissionList));
            }
        }
        return finalNode;
    }

    /**
     * 递归 放子菜单
     * @param permissionNode
     * @param permissionList
     * @return
     */
    private static AclPermission selectChildren(AclPermission permissionNode, List<AclPermission> permissionList) {
        //1 因为向一层菜单里面放二层菜单，二层里面还要放三层，把对象初始化
        permissionNode.setChildren(new ArrayList<AclPermission>());

        //2 遍历所有菜单list集合，进行判断比较，比较id和pid值是否相同
        for(AclPermission it : permissionList) {
            //判断 id和pid值是否相同
            if(permissionNode.getId().equals(it.getPid())) {
                //把父菜单的level值+1
                int level = permissionNode.getLevel()+1;
                it.setLevel(level);
                //如果children为空，进行初始化操作
                if(permissionNode.getChildren() == null) {
                    permissionNode.setChildren(new ArrayList<AclPermission>());
                }
                //把查询出来的子菜单放到父菜单里面
                permissionNode.getChildren().add(selectChildren(it,permissionList));
            }
        }
        return permissionNode;
    }

    /**
     * 根据当前菜单id，查询菜单里面子菜单id，封装到list集合 递归
     */
    private void selectPermissionChildById(String id, List<String> idList) {
        //查询菜单里面子菜单id
        QueryWrapper<AclPermission>  wrapper = new QueryWrapper<>();
        wrapper.eq("pid",id);
        wrapper.select("id");
        List<AclPermission> childIdList = permissionService.list(wrapper);
        //把childIdList里面菜单id值获取出来，封装idList里面，做递归查询
        childIdList.stream().forEach(item -> {
            //封装idList里面
            idList.add(item.getId());
            //递归查询
            this.selectPermissionChildById(item.getId(),idList);
        });
    }


    /**
     * 使用递归方法建菜单
     * @param treeNodes
     * @return
     */
    private static List<AclPermission> bulid(List<AclPermission> treeNodes) {
        List<AclPermission> trees = new ArrayList<>();
        for (AclPermission treeNode : treeNodes) {
            if ("0".equals(treeNode.getPid())) {
                treeNode.setLevel(1);
                trees.add(findChildren(treeNode,treeNodes));
            }
        }
        return trees;
    }

    /**
     * 递归查找子节点
     * @param treeNodes
     * @return
     */
    private static AclPermission findChildren(AclPermission treeNode,List<AclPermission> treeNodes) {
        treeNode.setChildren(new ArrayList<AclPermission>());

        for (AclPermission it : treeNodes) {
            if(treeNode.getId().equals(it.getPid())) {
                int level = treeNode.getLevel() + 1;
                it.setLevel(level);
                if (treeNode.getChildren() == null) {
                    treeNode.setChildren(new ArrayList<>());
                }
                treeNode.getChildren().add(findChildren(it,treeNodes));
            }
        }
        return treeNode;
    }

}
