package com.example.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.learning.common.exception.BusinessException;
import com.example.learning.common.result.ResultCode;
import com.example.learning.model.dto.PageQueryDTO;
import com.example.learning.model.entity.Tenant;
import com.example.learning.model.mapper.TenantMapper;
import com.example.learning.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 租户服务实现类
 */
@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    private TenantMapper tenantMapper;

    @Override
    public Page<Tenant> getPage(PageQueryDTO dto) {
        Page<Tenant> page = new Page<>(dto.getPage(), dto.getSize());
        return tenantMapper.selectPage(page, new LambdaQueryWrapper<Tenant>()
                .orderByDesc(Tenant::getCreatedAt));
    }

    @Override
    public Tenant getById(Long id) {
        Tenant tenant = tenantMapper.selectById(id);
        if (tenant == null) {
            throw new BusinessException(ResultCode.TENANT_NOT_FOUND);
        }
        return tenant;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tenant create(Tenant tenant) {
        tenant.setStatus(1);
        tenant.setCreatedAt(LocalDateTime.now());
        tenant.setUpdatedAt(LocalDateTime.now());
        tenantMapper.insert(tenant);
        return tenant;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tenant update(Long id, Tenant tenant) {
        Tenant existing = tenantMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.TENANT_NOT_FOUND);
        }

        if (tenant.getTenantCode() != null) {
            existing.setTenantCode(tenant.getTenantCode());
        }
        if (tenant.getTenantName() != null) {
            existing.setTenantName(tenant.getTenantName());
        }
        if (tenant.getContactName() != null) {
            existing.setContactName(tenant.getContactName());
        }
        if (tenant.getContactPhone() != null) {
            existing.setContactPhone(tenant.getContactPhone());
        }
        if (tenant.getLicenseCount() != null) {
            existing.setLicenseCount(tenant.getLicenseCount());
        }
        if (tenant.getExpireDate() != null) {
            existing.setExpireDate(tenant.getExpireDate());
        }
        if (tenant.getStatus() != null) {
            existing.setStatus(tenant.getStatus());
        }
        if (tenant.getConfig() != null) {
            existing.setConfig(tenant.getConfig());
        }
        existing.setUpdatedAt(LocalDateTime.now());

        tenantMapper.updateById(existing);
        return existing;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Tenant existing = tenantMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.TENANT_NOT_FOUND);
        }
        tenantMapper.deleteById(id);
    }
}
