package com.zyfgoup.utils;

import com.alibaba.fastjson.JSONObject;
import com.zyfgoup.entity.AclPermission;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据权限数据构建登录用户左侧菜单数据
 * @author
 * @since
 */
public class MenuHelper {

    /**
     * 构建菜单
     * @param treeNodes
     * @return
     */
    public static List<JSONObject> bulid(List<AclPermission> treeNodes) {
        List<JSONObject> menus = new ArrayList<>();
        if(treeNodes.size() == 1) {
            AclPermission topNode = treeNodes.get(0);
            //左侧一级菜单
            List<AclPermission> oneMeunList = topNode.getChildren();
            for(AclPermission one :oneMeunList) {
                JSONObject oneMeun = new JSONObject();
                oneMeun.put("path", one.getPath());
                oneMeun.put("component", one.getComponent());
                oneMeun.put("redirect", "noredirect");
                oneMeun.put("name", "name_"+one.getId());
                oneMeun.put("hidden", false);

                JSONObject oneMeta = new JSONObject();
                oneMeta.put("title", one.getName());
                oneMeta.put("icon", one.getIcon());
                oneMeun.put("meta", oneMeta);

                List<JSONObject> children = new ArrayList<>();
                List<AclPermission> twoMeunList = one.getChildren();
                for(AclPermission two :twoMeunList) {
                    JSONObject twoMeun = new JSONObject();
                    twoMeun.put("path", two.getPath());
                    twoMeun.put("component", two.getComponent());
                    twoMeun.put("name", "name_"+two.getId());
                    twoMeun.put("hidden", false);

                    JSONObject twoMeta = new JSONObject();
                    twoMeta.put("title", two.getName());
                    twoMeun.put("meta", twoMeta);

                    children.add(twoMeun);

                    List<AclPermission> threeMeunList = two.getChildren();
                    for(AclPermission three :threeMeunList) {
                        if(StringUtils.isEmpty(three.getPath())) continue;

                        JSONObject threeMeun = new JSONObject();
                        threeMeun.put("path", three.getPath());
                        threeMeun.put("component", three.getComponent());
                        threeMeun.put("name", "name_"+three.getId());
                        threeMeun.put("hidden", true);

                        JSONObject threeMeta = new JSONObject();
                        threeMeta.put("title", three.getName());
                        threeMeun.put("meta", threeMeta);

                        children.add(threeMeun);
                    }
                }
                oneMeun.put("children", children);
                menus.add(oneMeun);
            }
        }
        return menus;
    }
}
