package com.example.learning.model.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.learning.model.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * SysUser Mapper 接口
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
