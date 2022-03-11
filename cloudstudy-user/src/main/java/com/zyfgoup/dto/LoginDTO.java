package com.zyfgoup.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Zyfgoup
 * @Date 2021/1/20 20:10
 * @Description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDTO {
    private String mobile;
    private String password;
}
