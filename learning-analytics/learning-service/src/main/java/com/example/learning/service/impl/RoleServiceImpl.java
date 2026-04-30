package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.SysRole;
import com.example.learning.model.entity.SysRolePermission;
import com.example.learning.model.mapper.SysRoleMapper;
import com.example.learning.model.mapper.SysRolePermissionMapper;
import com.example.learning.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public Page<SysRole> getPage(PageQueryDTO dto) {
        Page<SysRole> page = new Page<>(dto.getPage(), dto.getSize());
        return sysRoleMapper.selectPage(page, new LambdaQueryWrapper<SysRole>()
                .orderByAsc(SysRole::getSortOrder)
                .orderByDesc(SysRole::getCreatedAt));
    }

    @Override
    public List<SysRole> getAllRoles() {
        return sysRoleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getStatus, 1)
                .orderByAsc(SysRole::getSortOrder));
    }

    @Override
    public SysRole getById(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        return role;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysRole create(SysRole role) {
        // 检查角色编码是否已存在
        Long count = sysRoleMapper.selectCount(
                new LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getRoleCode, role.getRoleCode())
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS);
        }

        role.setStatus(role.getStatus() != null ? role.getStatus() : 1);
        role.setSortOrder(role.getSortOrder() != null ? role.getSortOrder() : 0);
        role.setDataScope(role.getDataScope() != null ? role.getDataScope() : 1);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());

        sysRoleMapper.insert(role);
        return role;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysRole update(Long id, SysRole role) {
        SysRole existing = sysRoleMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 如果修改了角色编码，检查新编码是否已存在
        if (role.getRoleCode() != null && !role.getRoleCode().equals(existing.getRoleCode())) {
            Long count = sysRoleMapper.selectCount(
                    new LambdaQueryWrapper<SysRole>()
                            .eq(SysRole::getRoleCode, role.getRoleCode())
                            .ne(SysRole::getId, id)
            );
            if (count > 0) {
                throw new BusinessException(ResultCode.DATA_ALREADY_EXISTS);
            }
            existing.setRoleCode(role.getRoleCode());
        }

        if (role.getRoleName() != null) {
            existing.setRoleName(role.getRoleName());
        }
        if (role.getDataScope() != null) {
            existing.setDataScope(role.getDataScope());
        }
        if (role.getSortOrder() != null) {
            existing.setSortOrder(role.getSortOrder());
        }
        if (role.getStatus() != null) {
            existing.setStatus(role.getStatus());
        }
        if (role.getRemark() != null) {
            existing.setRemark(role.getRemark());
        }
        existing.setUpdatedAt(LocalDateTime.now());

        sysRoleMapper.updateById(existing);
        return existing;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        // 删除角色
        sysRoleMapper.deleteById(id);
        // 删除角色关联的权限
        sysRolePermissionMapper.delete(
                new LambdaQueryWrapper<SysRolePermission>()
                        .eq(SysRolePermission::getRoleId, id)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }

        // 先删除该角色的所有权限关联
        sysRolePermissionMapper.delete(
                new LambdaQueryWrapper<SysRolePermission>()
                        .eq(SysRolePermission::getRoleId, roleId)
        );

        // 批量插入新的权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            for (Long permissionId : permissionIds) {
                SysRolePermission rp = new SysRolePermission();
                rp.setRoleId(roleId);
                rp.setPermissionId(permissionId);
                rp.setTenantId(role.getTenantId());
                rp.setCreatedAt(now);
                rp.setUpdatedAt(now);
                sysRolePermissionMapper.insert(rp);
            }
        }
    }

    @Override
    public List<Long> getRolePermissionIds(Long roleId) {
        List<SysRolePermission> list = sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermission>()
                        .eq(SysRolePermission::getRoleId, roleId)
                        .select(SysRolePermission::getPermissionId)
        );
        return list.stream().map(SysRolePermission::getPermissionId).toList();
    }
}
