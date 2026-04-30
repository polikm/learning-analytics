package com.example.learning.admin.controller;

import com.example.learning.common.result.R;
import com.example.learning.model.dto.LoginDTO;
import com.example.learning.model.vo.LoginVO;
import com.example.learning.model.vo.UserVO;
import com.example.learning.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证管理Controller
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录
     *
     * @param dto 登录请求DTO
     * @return 登录响应VO（包含token和用户信息）
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO loginVO = authService.login(dto);
        return R.ok(loginVO);
    }

    /**
     * 用户登出
     */
    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public R<Void> logout() {
        authService.logout();
        return R.ok();
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息VO
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/user-info")
    public R<UserVO> getUserInfo() {
        UserVO userVO = authService.getUserInfo();
        return R.ok(userVO);
    }

    /**
     * 刷新Token
     *
     * @return 登录响应VO（包含新token和用户信息）
     */
    @Operation(summary = "刷新Token")
    @PostMapping("/refresh-token")
    public R<LoginVO> refreshToken() {
        LoginVO loginVO = authService.refreshToken();
        return R.ok(loginVO);
    }
}
