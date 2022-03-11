package com.zyfgoup.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author Zyfgoup
 * @Date 2021/1/5 18:52
 * @Description 在前端展示的用户信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {
    private String id;
    private String nickname;
    private List<String> roles;
    private String avatar;
    private String sign;
    private String mobile;
    /**
     * 性别 1 男，2 女
     */
    private Integer sex;

    /**
     * 年龄
     */
    private Integer age;

    //是否是管理端用户
    private boolean isAdmin;

}
