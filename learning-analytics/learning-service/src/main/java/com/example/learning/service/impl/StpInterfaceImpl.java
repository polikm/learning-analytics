package com.example.learning.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.learning.model.entity.SysPermission;
import com.example.learning.model.entity.SysRole;
import com.example.learning.model.entity.SysRolePermission;
import com.example.learning.model.entity.SysUserRole;
import com.example.learning.model.mapper.SysPermissionMapper;
import com.example.learning.model.mapper.SysRoleMapper;
import com.example.learning.model.mapper.SysRolePermissionMapper;
import com.example.learning.model.mapper.SysUserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sa-Token 权限认证接口实现类
 * <p>
 * 实现 StpInterface 接口，为 Sa-Token 提供用户角色和权限列表。
 * </p>
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    /**
     * 返回指定账号id所拥有的权限码集合
     *
     * @param loginId   登录id（即用户id）
     * @param loginType 登录类型
     * @return 权限码列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());

        // 1. 查询用户关联的角色ID列表
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, userId)
        );
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        // 2. 查询角色关联的权限ID列表
        List<SysRolePermission> rolePermissions = sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>()
                        .in(SysRolePermission::getRoleId, roleIds)
        );
        if (rolePermissions.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> permissionIds = rolePermissions.stream()
                .map(SysRolePermission::getPermissionId)
                .distinct()
                .collect(Collectors.toList());

        // 3. 查询权限码
        List<SysPermission> permissions = sysPermissionMapper.selectList(
                new LambdaQueryWrapper<SysPermission>()
                        .in(SysPermission::getId, permissionIds)
        );

        return permissions.stream()
                .map(SysPermission::getPermissionCode)
                .collect(Collectors.toList());
    }

    /**
     * 返回指定账号id所拥有的角色标识集合
     *
     * @param loginId   登录id（即用户id）
     * @param loginType 登录类型
     * @return 角色标识列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());

        // 1. 查询用户关联的角色ID列表
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, userId)
        );
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        // 2. 查询角色信息
        List<SysRole> roles = sysRoleMapper.selectList(
                new LambdaQueryWrapper<SysRole>()
                        .in(SysRole::getId, roleIds)
        );

        return roles.stream()
                .map(SysRole::getRoleCode)
                .collect(Collectors.toList());
    }
}
