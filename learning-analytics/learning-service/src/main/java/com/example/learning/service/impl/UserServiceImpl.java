package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.dto.UserDTO;
import com.example.learning.model.entity.SysUser;
import com.example.learning.model.mapper.SysUserMapper;
import com.example.learning.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Page<SysUser> getPage(PageQueryDTO dto) {
        Page<SysUser> page = new Page<>(dto.getPage(), dto.getSize());
        return sysUserMapper.selectPage(page, new LambdaQueryWrapper<SysUser>()
                .orderByDesc(SysUser::getCreatedAt));
    }

    @Override
    public SysUser getById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser create(UserDTO dto) {
        // 检查用户名是否已存在
        Long count = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, dto.getUsername())
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setUserType(dto.getUserType());
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        sysUserMapper.insert(user);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser update(Long id, UserDTO dto) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 如果修改了用户名，检查新用户名是否已存在
        if (dto.getUsername() != null && !dto.getUsername().equals(user.getUsername())) {
            Long count = sysUserMapper.selectCount(
                    new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getUsername, dto.getUsername())
                            .ne(SysUser::getId, id)
            );
            if (count > 0) {
                throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
            }
            user.setUsername(dto.getUsername());
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getRealName() != null) {
            user.setRealName(dto.getRealName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getUserType() != null) {
            user.setUserType(dto.getUserType());
        }
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }
        user.setUpdatedAt(LocalDateTime.now());

        sysUserMapper.updateById(user);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        // 逻辑删除：将状态设置为0（禁用）
        user.setStatus(0);
        user.setUpdatedAt(LocalDateTime.now());
        sysUserMapper.updateById(user);
    }

    @Override
    public List<SysUser> getByTenantId(Long tenantId) {
        return sysUserMapper.selectList(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getTenantId, tenantId)
                        .orderByDesc(SysUser::getCreatedAt)
        );
    }
}
