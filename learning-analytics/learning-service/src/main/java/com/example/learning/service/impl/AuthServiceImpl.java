package com.example.learning.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.dto.LoginDTO;
import com.example.learning.model.entity.SysUser;
import com.example.learning.model.mapper.SysUserMapper;
import com.example.learning.model.vo.LoginVO;
import com.example.learning.model.vo.UserVO;
import com.example.learning.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 认证服务实现类
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SysUserMapper sysUserMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginVO login(LoginDTO dto) {
        // 1. 根据用户名查询用户
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, dto.getUsername())
        );
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 2. 校验用户状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 3. 验证密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 4. 执行登录（Sa-Token）
        StpUtil.login(user.getId());

        // 5. 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        sysUserMapper.updateById(user);

        // 6. 构建返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(StpUtil.getTokenValue());
        loginVO.setRefreshToken(StpUtil.getTokenValue());
        loginVO.setUserInfo(buildUserVO(user));

        return loginVO;
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public UserVO getUserInfo() {
        long userId = StpUtil.getLoginIdAsLong();
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return buildUserVO(user);
    }

    @Override
    public LoginVO refreshToken() {
        // 检查当前是否已登录
        long userId = StpUtil.getLoginIdAsLong();

        // 重新创建Token会话（延长有效期）
        StpUtil.logout();
        StpUtil.login(userId);

        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(StpUtil.getTokenValue());
        loginVO.setRefreshToken(StpUtil.getTokenValue());
        loginVO.setUserInfo(buildUserVO(user));

        return loginVO;
    }

    /**
     * 构建用户VO
     */
    private UserVO buildUserVO(SysUser user) {
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setRealName(user.getRealName());
        userVO.setPhone(user.getPhone());
        userVO.setEmail(user.getEmail());
        userVO.setUserType(user.getUserType());
        userVO.setStatus(user.getStatus());
        userVO.setTenantId(user.getTenantId());
        userVO.setCreatedAt(user.getCreatedAt());
        return userVO;
    }
}
