package com.example.learning.service;

import com.example.learning.model.dto.LoginDTO;
import com.example.learning.model.vo.LoginVO;
import com.example.learning.model.vo.UserVO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param dto 登录请求DTO
     * @return 登录响应VO（包含token和用户信息）
     */
    LoginVO login(LoginDTO dto);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息VO
     */
    UserVO getUserInfo();

    /**
     * 刷新Token（延长有效期）
     *
     * @return 登录响应VO（包含新token和用户信息）
     */
    LoginVO refreshToken();
}
