package com.example.learning.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户类型(1管理员/2教师/3家长/4学生)
     */
    private Integer userType;

    /**
     * 状态
     */
    private Integer status;
}
