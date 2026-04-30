package com.example.learning.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.learning.model.entity.Tenant;
import org.apache.ibatis.annotations.Mapper;

/**
 * Tenant Mapper 接口
 */
@Mapper
public interface TenantMapper extends BaseMapper<Tenant> {
}
