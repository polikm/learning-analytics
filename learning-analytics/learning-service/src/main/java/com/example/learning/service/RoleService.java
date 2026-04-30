package com.example.learning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.SysRole;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService {

    /**
     * 分页查询角色列表
     *
     * @param dto 分页查询DTO
     * @return 分页结果
     */
    Page<SysRole> getPage(PageQueryDTO dto);

    /**
     * 获取全部角色（下拉用）
     *
     * @return 角色列表
     */
    List<SysRole> getAllRoles();

    /**
     * 获取角色详情
     *
     * @param id 角色ID
     * @return 角色实体
     */
    SysRole getById(Long id);

    /**
     * 创建角色
     *
     * @param role 角色信息
     * @return 创建的角色实体
     */
    SysRole create(SysRole role);

    /**
     * 更新角色
     *
     * @param id   角色ID
     * @param role 角色信息
     * @return 更新后的角色实体
     */
    SysRole update(Long id, SysRole role);

    /**
     * 删除角色
     *
     * @param id 角色ID
     */
    void delete(Long id);

    /**
     * 分配权限
     *
     * @param roleId       角色ID
     * @param permissionIds 权限ID列表
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);

    /**
     * 获取角色权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getRolePermissionIds(Long roleId);
}
