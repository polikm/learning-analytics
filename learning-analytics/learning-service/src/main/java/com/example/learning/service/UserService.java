package com.example.learning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.dto.UserDTO;
import com.example.learning.model.entity.SysUser;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 分页查询用户列表
     *
     * @param dto 分页查询DTO
     * @return 分页结果
     */
    Page<SysUser> getPage(PageQueryDTO dto);

    /**
     * 获取用户详情
     *
     * @param id 用户ID
     * @return 用户实体
     */
    SysUser getById(Long id);

    /**
     * 创建用户
     *
     * @param dto 用户信息DTO
     * @return 创建的用户实体
     */
    SysUser create(UserDTO dto);

    /**
     * 更新用户
     *
     * @param id  用户ID
     * @param dto 用户信息DTO
     * @return 更新后的用户实体
     */
    SysUser update(Long id, UserDTO dto);

    /**
     * 删除用户（逻辑删除）
     *
     * @param id 用户ID
     */
    void delete(Long id);

    /**
     * 按租户查询用户列表
     *
     * @param tenantId 租户ID
     * @return 用户列表
     */
    List<SysUser> getByTenantId(Long tenantId);
}
