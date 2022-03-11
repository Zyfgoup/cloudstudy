package com.zyfgoup.vo;

import com.zyfgoup.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author Zyfgoup
 * @Date 2021/1/20 18:39
 * @Description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVo {
    Long current;
    Long size;
    Long total;
    List<User> userList;
}
