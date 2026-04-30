package com.example.learning.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.Tenant;

/**
 * 租户服务接口
 */
public interface TenantService {

    /**
     * 分页查询租户列表
     *
     * @param dto 分页查询DTO
     * @return 分页结果
     */
    Page<Tenant> getPage(PageQueryDTO dto);

    /**
     * 获取租户详情
     *
     * @param id 租户ID
     * @return 租户实体
     */
    Tenant getById(Long id);

    /**
     * 创建租户
     *
     * @param tenant 租户实体
     * @return 创建的租户实体
     */
    Tenant create(Tenant tenant);

    /**
     * 更新租户
     *
     * @param id     租户ID
     * @param tenant 租户实体
     * @return 更新后的租户实体
     */
    Tenant update(Long id, Tenant tenant);

    /**
     * 删除租户
     *
     * @param id 租户ID
     */
    void delete(Long id);
}
